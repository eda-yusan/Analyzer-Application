package com.project.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Getter
@Setter
@Entity
public class Repository {

    @Id
    private Long id;

    private String name;

    @JsonProperty("stargazers_count")  //Bu anotasyonlar, gelen JSON verisinin alan adlarını Java alanlarına map eder (eşler)  stargazers_count=stargazersCount
    private int stargazersCount;

    @JsonProperty("watchers_count")
    private int watchersCount;

    private String language;

    @JsonProperty("open_issues_count")
    private int openIssuesCount;

    private String licenseName;

    @JsonProperty("license")
    private void unpackLicense(Map<String, Object> license) {  //	Bu nested JSON içinden name alanını çekip licenseName alanına set eder.
        if (license != null && license.get("name") != null) {
            this.licenseName = license.get("name").toString();
        }
        //GitHub API’sindeki license alanı şöyle gelir:
//        "license": {
//            "key": "mit",
//            "name": "MIT License",
//             ...
//        }
        //	Bu nested JSON içinden name alanını çekip licenseName alanına set eder.
    }

}

//Özetle:

//Bu sınıf şunları yapar:
//        •	GitHub API’den çekilen repository verilerini karşılar.
//	      •	JSON ile gelen verileri doğru alanlara map eder.
//        •	Veritabanında saklanabilir hale getirir (@Entity).
//        •	Nested yapıdaki license alanını özel olarak işler.


//Alan (fields)
//İd: GitHub repository’nin benzersiz kimliği. (Primary key)
//name: Repository’nin adı
//stargazersCount: Repository’nin aldığı yıldız (star) sayısı
//watchersCount: Takip eden sayısı (yeni sistemde star sayısıyla aynı olabilir)
//language: Yazılım dili (örneğin: Java, Python)
//openIssuesCount: Açık (çözülmemiş) issue sayısı
//licenseName: Lisans adı (MIT, Apache 2.0 vs.)
