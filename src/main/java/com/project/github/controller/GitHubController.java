
//package com.project.github.controller;
//
//import com.project.github.service.GitHubService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController   //Bu anotasyon, sınıfın bir REST controller olduğunu belirtir.
//public class GitHubController {   //Yani bu sınıftaki metotlar, HTTP isteklerine yanıt verir ve genellikle JSON (veya düz metin) döner.
//
//    private final GitHubService gitHubService;
//
//    public GitHubController(GitHubService gitHubService) {
//        this.gitHubService = gitHubService;
//    }
////    GitHubService sınıfı controller’a constructor(yapıcı metod) üzerinden enjekte edilir (Dependency Injection).
////  	Spring bu servisi(GitHubService) otomatik olarak bulur ve bu sınıfa verir.
////    	Bu servis, GitHub verilerini çekme ve kaydetme işlemlerini içeriyor.
//
//    @GetMapping("/fetch")
//    public String fetchGitHubData() {
//        gitHubService.fetchAndSaveData();
//        return "GitHub data fetched and saved successfully!";
//    }
//
////    Bu metot, HTTP GET isteğiyle /fetch adresine yapılan çağrılara yanıt verir.
////    	Metot çalıştığında:
////   1.	gitHubService.fetchAndSaveData() metodu çağrılır. Bu metot GitHub API’lerinden veri çeker ve veritabanına kaydeder.
////   2.	Ardından kullanıcıya "GitHub data fetched and saved successfully!" şeklinde düz bir yanıt döner.
//}
//
//
////Özetle:
////        •	Bu sınıf bir controller’dır ve /fetch URL’si üzerinden çağrıldığında:
////        •	GitHubService kullanarak GitHub verilerini çeker ve kaydeder.
////	•	Başarılı olduğunu belirten bir mesaj döner.

package com.project.github.controller;
import com.project.github.dto.ContributorDTO;
import com.project.github.dto.RepositoryDTO;
import com.project.github.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/fetch")
    public List<RepositoryDTO> fetchGitHubData() {
        Map<String, List<?>> data = gitHubService.fetchAndSaveData();
        return (List<RepositoryDTO>) data.get("repositories");
    }

    @GetMapping("/contributors")
    public List<ContributorDTO> getContributors() {
        return gitHubService.getAllContributors();
    }
}