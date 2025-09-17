package com.project.github;

import com.project.github.model.Repository;
import com.project.github.repository.ContributorRepository;
import com.project.github.repository.RepositoryRepository;
import com.project.github.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class GitHubServiceEdgeCasesTest {

    @Autowired
    private GitHubService gitHubService;

    @MockBean
    private RepositoryRepository repositoryRepository;

    @MockBean
    private ContributorRepository contributorRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testFetchAndSaveData_whenRepositoryListIsNull_shouldDoNothing() {
        when(restTemplate.exchange(
                // GitHub API'den dönen yanıt NULL array
                eq("https://api.github.com/orgs/apache/repos?per_page=100&sort=updated"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        gitHubService.fetchAndSaveData();

        verify(repositoryRepository, never()).save(any());
        verify(contributorRepository, never()).save(any());

    }

    @Test
    public void testFetchAndSaveData_whenRepositoryListIsEmpty_shouldDoNothing() {
        when(restTemplate.exchange(
                // GitHub API'den dönen yanıt boş array
                eq("https://api.github.com/orgs/apache/repos?per_page=100&sort=updated"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class)
        )).thenReturn(new ResponseEntity<>(new Repository[0], HttpStatus.OK));

        gitHubService.fetchAndSaveData();

        verify(repositoryRepository, never()).save(any());
        verify(contributorRepository, never()).save(any());
    }

    @Test
    public void testFetchAndSaveData_whenContributorsAreNull_shouldOnlySaveRepository() {
        Repository repo = new Repository(); 
        repo.setId(1L);
        repo.setName("repo-1");
        repo.setStargazersCount(100);
        repo.setWatchersCount(50);
        repo.setLanguage("Java");
        repo.setOpenIssuesCount(5);
        repo.setLicenseName("Apache License");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class)
        )).thenReturn(new ResponseEntity<>(new Repository[]{repo}, HttpStatus.OK));

        when(restTemplate.exchange(
                contains("/repos/apache/repo-1/contributors"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map[].class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        gitHubService.fetchAndSaveData();

        verify(repositoryRepository, times(1)).save(any());  // sadece repo kaydedilmeli
        verify(contributorRepository, never()).save(any());

        //Eğer repo varsa ama contributorlar null dönerse sadece repo kaydedilmeli.
    }

    @Test
    public void testFetchAndSaveData_whenContributorExists_shouldSkipSaving() {
        Repository repo = new Repository();
        repo.setId(1L);
        repo.setName("repo-1");
        repo.setStargazersCount(100);

        Map<String, Object> contributorMap = new HashMap<>();     
        contributorMap.put("login", "existing-user");   
        contributorMap.put("contributions", 50);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("location", "Earth");
        userMap.put("company", "Test Inc.");

        when(restTemplate.exchange(
                eq("https://api.github.com/orgs/apache/repos?per_page=100&sort=updated"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class)
        )).thenReturn(new ResponseEntity<>(new Repository[]{repo}, HttpStatus.OK));

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/apache/repo-1/contributors"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map[].class)
        )).thenReturn(new ResponseEntity<>(new Map[]{contributorMap}, HttpStatus.OK));

        when(restTemplate.exchange(
                eq("https://api.github.com/users/existing-user"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(userMap, HttpStatus.OK));

        when(contributorRepository.existsByRepositoryNameAndUsername("repo-1", "existing-user"))
                .thenReturn(true);

        gitHubService.fetchAndSaveData();

        verify(repositoryRepository).save(any()); //repo kaydeilmeli
        verify(contributorRepository, never()).save(any()); 
    }
}
