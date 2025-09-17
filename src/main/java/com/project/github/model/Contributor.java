//package com.project.github.model;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Entity  
//@Table(
//        uniqueConstraints = {@UniqueConstraint(columnNames = {"repositoryName", "username"})}
//)

//public class Contributor {
//
//    @Id  
//    @GeneratedValue(strategy = GenerationType.IDENTITY) 
//    private Long id;
//    private String repositoryName;
//    private String username;
//    private String location;
//    private String company;
//    private int contributions;
//
//
//}


package com.project.github.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"repositoryName", "username"})})
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String repositoryName;
    private String username;
    private String location;
    private String company;
    private int contributions;

    // Getter ve Setter metodları
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
