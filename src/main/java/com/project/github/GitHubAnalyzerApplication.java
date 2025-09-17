package com.project.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-secret.properties")
public class GitHubAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitHubAnalyzerApplication.class, args);
//        Java uygulamasının başlatıldığı yerdir.
//	•	SpringApplication.run(...) çağrısı:
//	•	Spring context’i başlatır.
//	•	Otomatik konfigürasyonları yapar.
//	•	Tüm bean’leri oluşturur.
//	•	Uygulamayı ayakta tutan bir embedded server (Tomcat) başlatır.
    }
}

//@SpringBootApplication

//Bu anotasyon 3 önemli anotasyonu birleştirir:
//        1.	@Configuration: Bu sınıf bir Spring config (yapılandırma) sınıfıdır.
//        2.	@EnableAutoConfiguration: Spring Boot’un otomatik konfigürasyon özelliklerini aktif eder.
//        3.	@ComponentScan: Uygulamanın bulunduğu paketteki tüm bileşenleri (@Service, @Controller, @Repository vb.) otomatik olarak tarar.
//
//Bu sayede uygulama “sıfır konfigürasyonla” çalışabilir hale gelir.

