package com.project.github;

import com.project.github.model.Repository;
import com.project.github.repository.RepositoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RepositoryRepositoryTest {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Test
    public void testSaveAndFindRepository() {
        Repository repo = new Repository();
        repo.setId(1L);
        repo.setName("test-repo");
        repo.setStargazersCount(50);

        repositoryRepository.save(repo);

        Repository found = repositoryRepository.findById(1L).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("test-repo");

//        1.	Yeni bir Repository nesnesi oluşturulur ve alanları doldurulur:
//	•	            id = 1
//	•	            name = "test-repo"
//	•	            stargazersCount = 50
//        2.	repositoryRepository.save(repo); ile nesne veritabanına kaydedilir.
//        3.	repositoryRepository.findById(1L) ile veritabanından aynı id’ye sahip repo aranır.
//        4.	Bulunursa (isNotNull()), test doğrulaması yapılır:
//	•	Bulunan repo nesnesinin adı "test-repo" olmalıdır.
//        5.	Eğer veri kaydedilip geri okunabiliyorsa test başarılıdır.

    }
}