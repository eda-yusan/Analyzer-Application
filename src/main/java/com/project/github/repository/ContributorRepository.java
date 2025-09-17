package com.project.github.repository;

import com.project.github.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository extends JpaRepository<Contributor, Long> { 

    boolean existsByRepositoryNameAndUsername(String repositoryName, String username);
}
