package com.project.github;

import com.project.github.model.Contributor;
import com.project.github.model.Repository;
import com.project.github.repository.ContributorRepository;
import com.project.github.repository.RepositoryRepository;
import com.project.github.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class GitHubServiceTest {

    @Autowired
    private GitHubService gitHubService;

    @MockBean
    private RepositoryRepository repositoryRepository;

    @MockBean                                                    //Gerçek veritabanı veya dış API kullanılmaz.Böylece dış dünyaya bağımlı olmadan sadece GitHubService’in davranışı test edilir
    private ContributorRepository contributorRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testFetchAndSaveData_shouldSaveTopRepositoriesAndContributors() {
        // Mock(sahte) repository listesi (100 repository, sadece 5'i kaydedilecek)
        Repository[] mockRepoArray = new Repository[100];
        for (int i = 0; i < 100; i++) {
            Repository repo = new Repository();
            repo.setId((long) (i + 1));
            repo.setName("mock-repo-" + (i + 1));
            repo.setStargazersCount(100 - i); // Azalan yıldız sayısı
            repo.setWatchersCount(50 - i / 2);
            repo.setLanguage("Language-" + (i % 5));
            repo.setOpenIssuesCount(i % 10);
            repo.setLicenseName("License-" + (i % 3));
            mockRepoArray[i] = repo;
        }

        // Her repo için 10 katkıcı mock'lama(taklit oluşturma)
        Map<String, Object>[] contributorMaps = new Map[10];
        for (int i = 0; i < 10; i++) {
            Map<String, Object> contributor = new HashMap<>();
            contributor.put("login", "mock-user-" + i);
            contributor.put("contributions", 42 + i);
            contributorMaps[i] = contributor;
        }

        // Contributor’lar için sahte kullanıcı bilgileri:
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("location", "Earth");
        userMap.put("company", "MockCompany");


        // Mock REST çağrıları. 103 e kadar
//        Testte gerçek GitHub API çağrısı yapılmaz. Bunun yerine restTemplate.exchange(...) çağrılarının döndürdüğü cevaplar önceden
//        belirlenmiş sahte (mock) veriler ile simüle edilir.



//        Eğer bu URL’ye bir GET isteği yapılırsa, gerçek sunucuya gitme, onun yerine mockRepoArray isimli
//    100 sahte repository nesnesini döndür.
        when(restTemplate.exchange(
                eq("https://api.github.com/orgs/apache/repos?per_page=100&sort=updated"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Repository[].class)
        )).thenReturn(new ResponseEntity<>(mockRepoArray, HttpStatus.OK));

        // Her repo için Contributor listesi için GitHub API mocklama (sadece ilk 5 repo için)
        for (int i = 0; i < 5; i++) {
            when(restTemplate.exchange(
                    eq("https://api.github.com/repos/apache/mock-repo-" + (i + 1) + "/contributors"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map[].class)
            )).thenReturn(new ResponseEntity<>(contributorMaps, HttpStatus.OK));
        }

        // Kullanıcı detayları için GitHub API mocklama
        for (int i = 0; i < 10; i++) {
            when(restTemplate.exchange(
                    eq("https://api.github.com/users/mock-user-" + i),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(new ResponseEntity<>(userMap, HttpStatus.OK));
        }


        // Repository ve Contributor save metodlarını mock'lama
        when(repositoryRepository.save(any(Repository.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(contributorRepository.save(any(Contributor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test metodu çağrılır. Bu çağrı sayesinde tüm yukarıdaki mock veriler kullanılarak gerçek metod test ediliyor.
        gitHubService.fetchAndSaveData();

        // Verilerin kaydedildiğini doğrula
        //İlk 5 en çok yıldızlı repo save() metodu ile kaydedilmiş mi?
        //	•	Kaydedilen repo’ların sırasıyla ismi mock-repo-1 … mock-repo-5 mi?
        ArgumentCaptor<Repository> repoCaptor = ArgumentCaptor.forClass(Repository.class);
        verify(repositoryRepository, times(5)).save(repoCaptor.capture());
        assertThat(repoCaptor.getAllValues()).hasSize(5);
        assertThat(repoCaptor.getAllValues().get(0).getName()).isEqualTo("mock-repo-1");
        assertThat(repoCaptor.getAllValues().get(1).getName()).isEqualTo("mock-repo-2");
        assertThat(repoCaptor.getAllValues().get(2).getName()).isEqualTo("mock-repo-3");
        assertThat(repoCaptor.getAllValues().get(3).getName()).isEqualTo("mock-repo-4");
        assertThat(repoCaptor.getAllValues().get(4).getName()).isEqualTo("mock-repo-5");


        ArgumentCaptor<Contributor> contributorCaptor = ArgumentCaptor.forClass(Contributor.class);
        verify(contributorRepository, times(50)).save(contributorCaptor.capture());
        assertThat(contributorCaptor.getAllValues()).hasSize(50);
        for (Contributor contributor : contributorCaptor.getAllValues()) {
            assertThat(contributor.getUsername()).startsWith("mock-user-");
            assertThat(contributor.getLocation()).isEqualTo("Earth");
            assertThat(contributor.getCompany()).isEqualTo("MockCompany");
            assertThat(contributor.getContributions()).isGreaterThanOrEqualTo(42);
//            	•	5 repo * 10 contributor = 50 kayıt var mı?
//	•	Her contributor’ın:
//	•	Username’i mock-user-* ile başlıyor mu?
//	•	Location = Earth
//	•	Company = MockCompany
//	•	Contributions sayısı ≥ 42 mi?

        }
    }
}

//Spring Boot ile geliştirdiğin GitHubService sınıfının fetchAndSaveData() metodunun düzgün çalışıp çalışmadığını
//kontrol etmek için yazılmıştır.