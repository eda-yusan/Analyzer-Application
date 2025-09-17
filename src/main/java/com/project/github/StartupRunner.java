package com.project.github;

import com.project.github.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner {

    @Autowired
    private GitHubService gitHubService;  //GitHubService sınıfı buraya otomatik olarak enjekte edilir (dependency injection).

    @EventListener(ApplicationReadyEvent.class)  //	Bu anotasyon sayesinde, uygulama hazır olduğunda (Spring tüm context’i başlattığında) bu metod otomatik olarak çağrılır.
    public void runAfterStartup() {
        gitHubService.fetchAndSaveData();
    }
}

//@Component Nedir?

//        •	Spring uygulamasında bir sınıfı “bean” yapmak için kullanılır.
//	      •	Bean, Spring’in yönetip kontrol ettiği, ihtiyaç duyulduğunda otomatik olarak yarattığı ve
//      bağımlılıklarını enjekte ettiği nesnedir.
//	      •	@Component ile işaretlenen sınıflar, Spring tarafından taranır ve otomatik olarak uygulama başlatılırken belleğe
//        (Spring konteynerine) alınır.

//private final GitHubService gitHubService;
//
//public StartupRunner(GitHubService gitHubService) {
//    this.gitHubService = gitHubService;
//}     @Autowired yerine constructor injection ile yapımı;