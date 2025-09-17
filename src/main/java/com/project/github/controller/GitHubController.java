package com.project.github.controller;
import com.project.github.dto.ContributorDTO;
import com.project.github.dto.RepositoryDTO;
import com.project.github.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/fetch")
    public List<RepositoryDTO> fetchGitHubData() {
        Map<String, List<?>> data = gitHubService.fetchAndSaveData();
        return (List<RepositoryDTO>) data.get("repositories");
    }

    @GetMapping("/contributors")
    public List<ContributorDTO> getContributors() {
        return gitHubService.getAllContributors();
    }
}
