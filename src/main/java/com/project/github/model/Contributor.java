//package com.project.github.model;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Entity  //	Bu anotasyon, Contributor sınıfının bir veritabanı tablosuna karşılık geldiğini belirtir.
////Spring Data JPA bu sınıfı kullanarak otomatik tablo oluşturabilir ve veritabanı işlemlerini yönetebilir.
//@Table(
//        uniqueConstraints = {@UniqueConstraint(columnNames = {"repositoryName", "username"})}
//)
////Bu, sınıfın(Contributor sınıfının) karşılık geldiği tabloya özel kurallar tanımlar.
////        •	repositoryName ve username sütunlarının birleşimi benzersiz (unique) olmalı demektir.
////        •	Aynı repoya aynı kullanıcı birden fazla kez kaydedilemesin diye bir güvenlik önlemidir.
//
//public class Contributor {
//
//    @Id  //id alanı birincil anahtar (primary key) olarak tanımlanmıştır.
//    @GeneratedValue(strategy = GenerationType.IDENTITY)   //GenerationType.IDENTITY: ID alanı veritabanında otomatik artan şekilde oluşturulur
//    //Bu anotasyon, @Id alanının değerini otomatik olarak oluştur demektir
//    private Long id;
//    private String repositoryName;
//    private String username;
//    private String location;
//    private String company;
//    private int contributions;
//
//
//}
////Özetle:
//
////Bu sınıf(contributor sınıfı), veritabanında her bir katkıcının bilgilerini tutmak için kullanılır:
////  •	Hangi repoya katkı yaptığı,
////	•	Kim olduğu,
////	•	Nereden olduğu,
////	•	Hangi şirkette çalıştığı,
////  •	Ne kadar katkı yaptığı gibi bilgiler tutulur.
////  •	@UniqueConstraint(repositoryName, username) sayesinde aynı kullanıcı aynı repo için veritabanına iki kez eklenemez.
////  •	Bu sınıf, @Entity olduğundan ContributorRepository gibi bir JpaRepository ile kolayca veritabanında saklanabilir, sorgulanabilir.
//
//
////Alan (fields)
//// id :Her katkıcının otomatik olarak artan benzersiz kimliği (primary key)
////repositoryName: Hangi GitHub reposuna katkı yaptığını belirtir
////username: Katkıcının GitHub kullanıcı adı
////location: Katkıcının lokasyonu (şehir, ülke vs.)
////company: Katkıcının çalıştığı şirket (GitHub profilinden)
////contributions: Katkıcının ilgili repoya yaptığı katkı sayısı
//
//
////JpaRepository: Veritabanı işlemlerini soyutlar ve SQL yazmadan CRUD işlemleri yapmayı sağlar. Örn: ContributorRepository
////JPA (Java Persistence API) ile entite sınıflarını veritabanıyla eşler.
//// getter setter metodları:	Bu metotlar sayesinde Spring/JPA bu nesne üzerinde veri okuyup yazabilir.


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