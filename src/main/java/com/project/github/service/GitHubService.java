package com.project.github.service;

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

@Service  //Spring tarafından otomatik olarak yönetilen (bean olarak tanınan) bir servis sınıfıdır.İş mantığı (business logic) burada yer alır.
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);
    private static final String BASE_URL = "https://api.github.com/orgs/apache/repos?per_page=100&sort=updated";

    private final RepositoryRepository repositoryRepository;
    private final ContributorRepository contributorRepository;
    private final RestTemplate restTemplate;

//    RepositoryRepository: Repo verilerini DB’ye kaydeder.
//    ContributorRepository: Contributor verilerini DB’ye kaydeder.
//    RestTemplate: GitHub API’den veri çekmek için HTTP istemcisi.

    @Value("${github.token}")
    private String githubToken;
//  application.properties den  GitHub Token’ını çeker.

    public GitHubService(RepositoryRepository repositoryRepository,
                         ContributorRepository contributorRepository,
                         RestTemplate restTemplate) {
        this.repositoryRepository = repositoryRepository;
        this.contributorRepository = contributorRepository;
        this.restTemplate = restTemplate;
        // RepositoryRepository , ContributorRepository , RestTemplate sınıfları servise constructor  üzerinden enjekte edilir (Dependency Injection).
    }

    private HttpEntity<String> createAuthEntity() {
        //createAuthEntity: GitHub API’ye yapılacak HTTP istekleri için yetkilendirme başlığı (Authorization Header) oluşturan yardımcı bir metottur.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken); //GitHub API, erişim için kişisel erişim token’ı (Personal Access Token - PAT) ister.
        return new HttpEntity<>(headers);
    }

    public void fetchAndSaveData() {
        try {
            logger.info("Fetching repositories from GitHub API...");
            ResponseEntity<Repository[]> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    createAuthEntity(), // ⬅️ İşte burada token'lı header gönderiliyor
                    Repository[].class  //GitHub’dan gelen JSON verisi → Repository[] dizisine çevrilir.
                    //GitHub Apache organizasyonundaki 100 repo çekilir
            );

            Repository[] repoArray = response.getBody();
            if (repoArray == null || repoArray.length == 0) {
                logger.warn("No repositories found from GitHub API.");
                return;
            }

            List<Repository> allRepos = Arrays.asList(repoArray);
            logger.info("Fetched {} repositories.", allRepos.size());
            // bu iki satır Bu kod parçası, Java'da bir dizi (repoArray) Repository nesnesini bir List<Repository>'e dönüştürüp,
            // ardından bu listenin boyutunu (kaç tane repository olduğunu) log'layan bir işlemdir.

            List<Repository> topRepos = allRepos.stream()  //allRepos.stream() → Tüm repolar üzerinde akış başlatılır.
                    .filter(repo -> repo != null && repo.getStargazersCount() > 0)   //filter, belirli koşulu sağlayan repolar filtrelenir
                    .sorted(Comparator.comparingInt(Repository::getStargazersCount).reversed()) // sorted, filtrelen repolar yıldız sayısına göre sıralanır
                    .limit(5)
                    .collect(Collectors.toList());  // Stream’i listeye çevir” demektir. Bu 5 elemanlık stream, bir List<Repository> nesnesine dönüştürülür.
            //En çok yıldıza sahip ilk 5 repo seçilir

            if (topRepos.isEmpty()) {
                logger.warn("No repositories with stargazers found.");
                return;
            }

            logger.info("Saving top {} repositories.", topRepos.size());
            for (Repository repo : topRepos) {
                logger.debug("Saving repository: {}", repo.getName());
                repositoryRepository.save(repo);   // Her repo,  Veritabanına kaydedilir:

                String contributorsUrl = String.format("https://api.github.com/repos/apache/%s/contributors", repo.getName());
                // her bir repo için katkıcı listesini alacak URL hazırlanır.      //Her repo için Katkıcılar API’den çekilir:
                ResponseEntity<Map[]> contributorsResponse = restTemplate.exchange(
                        contributorsUrl,
                        HttpMethod.GET,
                        createAuthEntity(),  // ⬅️ İşte burada token'lı header gönderiliyor
                        Map[].class
                );

                Map[] contributors = contributorsResponse.getBody();
                if (contributors == null || contributors.length == 0) {
                    logger.warn("No contributors found for repository: {}", repo.getName());
                    continue;
                }

                Arrays.stream(contributors)
                        .limit(10)   // İlk 10 katkıcı incelenir.
                        .forEach(c -> {
                            String username = (String) c.get("login");   //Katkıcının login adı ve katkı sayısı alınır:
                            Integer contributions = (Integer) c.get("contributions");

                            if (username != null && contributions != null) {
                                if (!contributorRepository.existsByRepositoryNameAndUsername(repo.getName(), username)) {
                                    //→ Bu kullanıcı, o repoya daha önce veritabanına kaydedilmemişse devam et.
                                    String userUrl = "https://api.github.com/users/" + username;
                                    ResponseEntity<Map> userResponse = restTemplate.exchange(
                                            userUrl,
                                            HttpMethod.GET,
                                            createAuthEntity(),
                                            Map.class
                                            //Bu satırda GitHub API’ye GET isteği gönderiliyor ve gelen cevabı Map formatında alıyor:
                                            //	•	restTemplate.exchange(...) → HTTP isteği atar.
                                            //	•	createAuthEntity() → İstekle birlikte authorization (token) bilgisi gönderir.
                                            //	•	Map.class → JSON cevabı Map objesi olarak al.
                                    );
//100-108 arası Eğer veritabanında olmayan bir kullanıcı varsa, onun GitHub profil bilgilerini GitHub API üzerinden çekmek için HTTP GET isteği gönderir.
                                    Map user = userResponse.getBody();

                                    Contributor contributor = new Contributor();
                                    //Yeni bir Contributor nesnesi oluşturulur.
                                    //Bu nesne JPA entity’sidir; yani veritabanında bir satırı temsil eder.
                                    contributor.setRepositoryName(repo.getName());  //Repository adı atanır:
                                    contributor.setUsername(username);  //Kullanıcı adı atanır:
                                    contributor.setContributions(contributions); //Katkı sayısı atanır:
                                    contributor.setLocation(user != null ? (String) user.get("location") : null);   //user map’i boş değilse location alanı alınır, değilse null atanır.
                                    contributor.setCompany(user != null ? (String) user.get("company") : null);

                                    logger.debug("Saving contributor: {} for repository: {}", username, repo.getName());
                                    contributorRepository.save(contributor);  //	•	ContributorRepository kullanılarak bu katkıcı veritabanına kaydedilir.
                                } else {
                                    logger.debug("Contributor {} already exists for repo {}. Skipping.", username, repo.getName());
                                }
                            }
                        });
            }

            // === VERİYİ DOSYAYA YAZDIR ===
            List<Repository> repos = repositoryRepository.findAll();     // bu iki saturla veritabanından tüm veriler tekrar çekilir
            List<Contributor> allContributors = contributorRepository.findAll();

            List<String> outputLines = new ArrayList<>();
            outputLines.add("=== Repositories ===");
            repos.forEach(r -> outputLines.add(
                    String.format("repo: %s, stars: %d", r.getName(), r.getStargazersCount()))
            );

            outputLines.add("\n=== Contributors ===");
            allContributors.forEach(c -> outputLines.add(
                    String.format("repo: %s, user: %s, location: %s, company: %s, contributions: %d",
                            c.getRepositoryName(),
                            c.getUsername(),
                            c.getLocation() != null ? c.getLocation() : "N/A",
                            c.getCompany() != null ? c.getCompany() : "N/A",
                            c.getContributions()))
            );

            OutputWriter.writeToFile(outputLines, "output.txt");   //dosyaya yazdır

            // === Konsola da yazdır (isteğe bağlı) ===
            System.out.println("=== Repositories ===");
            repos.forEach(r -> System.out.printf("repo: %s, stars: %d%n", r.getName(), r.getStargazersCount()));

            System.out.println("=== Contributors ===");
            allContributors.forEach(c ->
                    System.out.printf("repo: %s, user: %s, location: %s, company: %s, contributions: %d%n",
                            c.getRepositoryName(), c.getUsername(), c.getLocation(), c.getCompany(), c.getContributions())
            );

        } catch (Exception e) {
            logger.error("Error fetching data from GitHub API: {}", e.getMessage(), e);  //Herhangi bir hata olursa loglanır, uygulama çökmez.
        }
    }
}


//Özetle:
//
//Bu servis şunu yapar:
//        1.	GitHub API’den son güncellenmiş 100 reposunu çeker.
//        2.	En çok yıldız almış ilk 5 repoyu seçer.
//        3.	Bu repoların ilk 10 katkıcısını getirir.
//        4.	Her katkıcının detaylarını (company, location) çeker.
//        5.	Veritabanına kayıt eder (aynı kişi ve repo varsa kaydetmez).
//        6.	Hem console’a hem de output.txt dosyasına yazar.