package com.project.github.dto;

import lombok.Data;

@Data
public class ContributorDto {
    private String repositoryName;
    private String username;
    private String location;
    private String company;
    private int contributions;
}
