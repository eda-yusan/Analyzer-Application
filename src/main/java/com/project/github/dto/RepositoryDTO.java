//package com.project.github.dto;
//
//import lombok.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class RepositoryDTO {
//    private String name;
//    private int stargazersCount;
//}

package com.project.github.dto;

public class RepositoryDTO {
    private String name;
    private int stargazersCount;

    public RepositoryDTO() {}

    public RepositoryDTO(String name, int stargazersCount) {
        this.name = name;
        this.stargazersCount = stargazersCount;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStargazersCount() { return stargazersCount; }
    public void setStargazersCount(int stargazersCount) { this.stargazersCount = stargazersCount; }
}