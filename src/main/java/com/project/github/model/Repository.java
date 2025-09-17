//package com.project.github.model;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Getter;
//import lombok.Setter;
//
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import java.util.Map;
//
//@Getter
//@Setter
//@Entity
//public class Repository {
//
//    @Id
//    private Long id;
//
//    private String name;
//
//    @JsonProperty("stargazers_count")  
//    private int stargazersCount;
//
//    @JsonProperty("watchers_count")
//    private int watchersCount;
//
//    private String language;
//
//    @JsonProperty("open_issues_count")
//    private int openIssuesCount;
//
//    private String licenseName;
//
//    @JsonProperty("license")
//    private void unpackLicense(Map<String, Object> license) {  
//        if (license != null && license.get("name") != null) {
//            this.licenseName = license.get("name").toString();
//        }
//    }
//
//}

package com.project.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class Repository {

    @Id
    private Long id;

    private String name;

    @JsonProperty("stargazers_count")
    private int stargazersCount;

    @JsonProperty("watchers_count")
    private int watchersCount;

    private String language;

    @JsonProperty("open_issues_count")
    private int openIssuesCount;

    private String licenseName;

    @JsonProperty("license")
    private void unpackLicense(Map<String, Object> license) {
        if (license != null && license.get("name") != null) {
            this.licenseName = license.get("name").toString();
        }
    }

    // Getter ve Setter metodları
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStargazersCount() { return stargazersCount; }
    public void setStargazersCount(int stargazersCount) { this.stargazersCount = stargazersCount; }

    public int getWatchersCount() { return watchersCount; }
    public void setWatchersCount(int watchersCount) { this.watchersCount = watchersCount; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getOpenIssuesCount() { return openIssuesCount; }
    public void setOpenIssuesCount(int openIssuesCount) { this.openIssuesCount = openIssuesCount; }

    public String getLicenseName() { return licenseName; }
    public void setLicenseName(String licenseName) { this.licenseName = licenseName; }
}
