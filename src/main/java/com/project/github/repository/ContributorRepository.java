package com.project.github.repository;

import com.project.github.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository extends JpaRepository<Contributor, Long> { //	Bu interface, JpaRepository arayüzünden kalıtım alır.
    //	•	JpaRepository<Contributor, Long>:
    //	•	Contributor: Hangi entity için çalışacağı
    //	•	Long: Primary key türü (id alanı Long türündeydi)
//    JpaRepository aracılığıyla saplanan hazır metotlar:
//    save(Contributor entity)
//    findById(Long id)
//    findAll()
//    deleteById(Long id)
//    count()
//    existsById(Long id)

    boolean existsByRepositoryNameAndUsername(String repositoryName, String username);
//    Bu metot, verilen repositoryName ve username ile eşleşen bir Contributor veritabanında var mı diye kontrol eder.
//	 Spring Data JPA, metot ismine bakarak otomatik olarak SQL sorgusu oluşturur:
//    SELECT COUNT(*) > 0
//    FROM contributor
//    WHERE repository_name = ? AND username = ?
}


//Özetle:
//        •	ContributorRepository, Contributor entity’siyle ilgili tüm veritabanı işlemlerini yönetir.
//        •	Spring JPA’nın sunduğu hazır CRUD fonksiyonlarını kullanır.
//        •	existsByRepositoryNameAndUsername() metodu sayesinde aynı kullanıcı aynı repo için tekrar
//eklenmeden önce kontrol yapılabilir.