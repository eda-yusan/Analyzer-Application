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

    }
}
