package com.project.github.service;

import com.project.github.dto.ContributorDTO;
import com.project.github.dto.RepositoryDTO;
import com.project.github.model.Contributor;
import com.project.github.model.Repository;
import com.project.github.repository.ContributorRepository;
import com.project.github.repository.RepositoryRepository;
import com.project.github.util.OutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);
    private static final String BASE_URL = "https://api.github.com/orgs/apache/repos?per_page=100&sort=updated";

    private final RepositoryRepository repositoryRepository;
    private final ContributorRepository contributorRepository;
    private final RestTemplate restTemplate;

    @Value("${github.token}")
    private String githubToken;

    public GitHubService(RepositoryRepository repositoryRepository,
                         ContributorRepository contributorRepository,
                         RestTemplate restTemplate) {
        this.repositoryRepository = repositoryRepository;
        this.contributorRepository = contributorRepository;
        this.restTemplate = restTemplate;
    }

    private HttpEntity<String> createAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        return new HttpEntity<>(headers);
    }

    /**
     * Repos ve Contributors'u DB'ye kaydeder ve DTO olarak döner
     */
    public Map<String, List<?>> fetchAndSaveData() {
        List<RepositoryDTO> repoDTOs = new ArrayList<>();
        List<ContributorDTO> contributorDTOs = new ArrayList<>();

        try {
            logger.info("Fetching repositories from GitHub API...");
            ResponseEntity<Repository[]> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    createAuthEntity(),
                    Repository[].class
            );

            Repository[] repoArray = response.getBody();
            if (repoArray == null || repoArray.length == 0) {
                logger.warn("No repositories found from GitHub API.");
                return Map.of(
                        "repositories", repoDTOs,
                        "contributors", contributorDTOs
                );
            }

            List<Repository> allRepos = Arrays.asList(repoArray);
            logger.info("Fetched {} repositories.", allRepos.size());

            // Top 5 repo by stargazers
            List<Repository> topRepos = allRepos.stream()
                    .filter(repo -> repo != null && repo.getStargazersCount() > 0)
                    .sorted(Comparator.comparingInt(Repository::getStargazersCount).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            if (topRepos.isEmpty()) {
                logger.warn("No repositories with stargazers found.");
                return Map.of(
                        "repositories", repoDTOs,
                        "contributors", contributorDTOs
                );
            }

            logger.info("Saving top {} repositories.", topRepos.size());
            for (Repository repo : topRepos) {
                repositoryRepository.save(repo);
                repoDTOs.add(new RepositoryDTO(repo.getName(), repo.getStargazersCount()));

                // Contributors
                String contributorsUrl = String.format("https://api.github.com/repos/apache/%s/contributors", repo.getName());
                ResponseEntity<Map[]> contributorsResponse = restTemplate.exchange(
                        contributorsUrl,
                        HttpMethod.GET,
                        createAuthEntity(),
                        Map[].class
                );

                Map[] contributors = contributorsResponse.getBody();
                if (contributors == null || contributors.length == 0) {
                    logger.warn("No contributors found for repository: {}", repo.getName());
                    continue;
                }

                Arrays.stream(contributors)
                        .limit(10)
                        .forEach(c -> {
                            String username = (String) c.get("login");
                            Integer contributions = (Integer) c.get("contributions");

                            if (username != null && contributions != null) {
                                if (!contributorRepository.existsByRepositoryNameAndUsername(repo.getName(), username)) {
                                    // Kullanıcı detayları
                                    String userUrl = "https://api.github.com/users/" + username;
                                    ResponseEntity<Map> userResponse = restTemplate.exchange(
                                            userUrl,
                                            HttpMethod.GET,
                                            createAuthEntity(),
                                            Map.class
                                    );
                                    Map user = userResponse.getBody();

                                    Contributor contributor = new Contributor();
                                    contributor.setRepositoryName(repo.getName());
                                    contributor.setUsername(username);
                                    contributor.setContributions(contributions);
                                    contributor.setLocation(user != null ? (String) user.get("location") : null);
                                    contributor.setCompany(user != null ? (String) user.get("company") : null);

                                    contributorRepository.save(contributor);

                                    contributorDTOs.add(new ContributorDTO(
                                            repo.getName(),
                                            username,
                                            user != null ? (String) user.get("location") : null,
                                            user != null ? (String) user.get("company") : null,
                                            contributions
                                    ));
                                }
                            }
                        });
            }

            // Dosyaya yazdır (isteğe bağlı)
            List<String> outputLines = new ArrayList<>();
            outputLines.add("=== Repositories ===");
            repoDTOs.forEach(r -> outputLines.add(String.format("repo: %s, stars: %d", r.getName(), r.getStargazersCount())));
            outputLines.add("\n=== Contributors ===");
            contributorDTOs.forEach(c -> outputLines.add(
                    String.format("repo: %s, user: %s, location: %s, company: %s, contributions: %d",
                            c.getRepositoryName(),
                            c.getUsername(),
                            c.getLocation() != null ? c.getLocation() : "N/A",
                            c.getCompany() != null ? c.getCompany() : "N/A",
                            c.getContributions()))
            );
            OutputWriter.writeToFile(outputLines, "output.txt");

        } catch (Exception e) {
            logger.error("Error fetching data from GitHub API: {}", e.getMessage(), e);
        }

        return Map.of(
                "repositories", repoDTOs,
                "contributors", contributorDTOs
        );
    }

    /**
     * Tüm contributorları DTO olarak döner
     */
    public List<ContributorDTO> getAllContributors() {
        return contributorRepository.findAll()
                .stream()
                .map(c -> new ContributorDTO(
                        c.getRepositoryName(),
                        c.getUsername(),
                        c.getLocation(),
                        c.getCompany(),
                        c.getContributions()
                ))
                .collect(Collectors.toList());
    }
}

