package com.project.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-secret.properties")
public class GitHubAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitHubAnalyzerApplication.class, args);

    }
}


