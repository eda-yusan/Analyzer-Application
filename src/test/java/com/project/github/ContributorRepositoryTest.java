package com.project.github;
import com.project.github.model.Contributor;
import com.project.github.repository.ContributorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ContributorRepositoryTest {

    @Autowired
    private ContributorRepository contributorRepository;  

    @Test
    void testSaveAndExists() {
        Contributor c = new Contributor();
        c.setUsername("test-user");
        c.setRepositoryName("repo-x");
        c.setContributions(30);
        contributorRepository.save(c);

        boolean exists = contributorRepository.existsByRepositoryNameAndUsername("repo-x", "test-user");
        assertThat(exists).isTrue();

    }
}
