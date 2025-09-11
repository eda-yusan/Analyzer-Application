package com.project.github;

import com.project.github.model.Contributor;
import com.project.github.repository.ContributorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//DataJpaTest, Spring Data JPA repository'lerini test etmek için optimize edilmiş bir anotasyondur. Sadece JPA ile ilgili bileşenleri yükler,
//in-memory veritabanı kullanır ve testlerin hızlı, izole çalışmasını sağlar
public class ContributorRepositoryTest {

    @Autowired
    private ContributorRepository contributorRepository;  //ContributorRepository sınıfı buraya otomatik olarak enjekte edilir (dependency injection).

    @Test
    void testSaveAndExists() {
        Contributor c = new Contributor();
        c.setUsername("test-user");
        c.setRepositoryName("repo-x");
        c.setContributions(30);
        contributorRepository.save(c);

        boolean exists = contributorRepository.existsByRepositoryNameAndUsername("repo-x", "test-user");
        assertThat(exists).isTrue();

//        1.	Yeni bir Contributor nesnesi oluşturulur ve gerekli alanlar doldurulur:
//	•	username: "test-user"
//	•	repositoryName: "repo-x"
//	•	contributions: 30
//        2.	Bu nesne contributorRepository.save(c) ile veritabanına kaydedilir.
//        3.	Ardından existsByRepositoryNameAndUsername("repo-x", "test-user") metodu çağrılır ve kayıt olup olmadığı sorgulanır.
//        4.	assertThat(exists).isTrue(); ile test doğrulanır:
//	•	Eğer kayıt başarılıysa ve sorgu doğru çalışıyorsa exists true döner ve test geçer.
//	•	Aksi halde test başarısız olur.

    }
}