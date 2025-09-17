//package com.project.github.dto;
//
//import lombok.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class ContributorDTO {
//    private String repositoryName;
//    private String username;
//    private String location;
//    private String company;
//    private int contributions;
//}

package com.project.github.dto;

public class ContributorDTO {
    private String repositoryName;
    private String username;
    private String location;
    private String company;
    private int contributions;

    public ContributorDTO() {}

    public ContributorDTO(String repositoryName, String username, String location, String company, int contributions) {
        this.repositoryName = repositoryName;
        this.username = username;
        this.location = location;
        this.company = company;
        this.contributions = contributions;
    }

    public String getRepositoryName() { return repositoryName; }
    public void setRepositoryName(String repositoryName) { this.repositoryName = repositoryName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public int getContributions() { return contributions; }
    public void setContributions(int contributions) { this.contributions = contributions; }
}