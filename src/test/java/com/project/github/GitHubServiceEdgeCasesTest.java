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

//        Amaç: API’den gelen repo listesi null ise hiçbir veri kaydedilmemelidir.
//        Doğrulama: save() metodlarının hiçbiri çağrılmamalı (never()).

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
//
//        API’den gelen repo listesi boş ise hiçbir veri kaydedilmemelidir.
//                Doğrulama: save() metodlarının hiçbiri çağrılmamalı (never()).
    }

    @Test
    public void testFetchAndSaveData_whenContributorsAreNull_shouldOnlySaveRepository() {
        Repository repo = new Repository(); // örnek bir repo oluşturulmuş
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

        Map<String, Object> contributorMap = new HashMap<>();     //Map burada sadece testler için mock veri üretimi amacıyla kullanılıyor. Amaç: GitHub’tan gelen JSON’ları test ortamında model sınıfı olmadan canlandırmak.
        contributorMap.put("login", "existing-user");   //Map kullanılmasının nedeni, GitHub API’den gelen JSON yanıtlarını doğrudan Java nesneleriyle eşleştirmek yerine daha esnek bir veri yapısı ile temsil etmektir.
        //Model sınıfı oluşturmadan Map ile canlandırmak testlerde hızlı, esnek ve pratik bir çözümdür. Ama gerçek projelerde ve daha kapsamlı testlerde tip güvenliği, okunabilirlik ve bakım kolaylığı için mutlaka model/DTO sınıfları tercih edilir.
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
        verify(contributorRepository, never()).save(any()); //katkıcı kaydeddilmemeli

        //Aynı kullanıcı-repo ilişkisi zaten varsa, Contributor tekrar kayıt edilmemeli.
    }
}