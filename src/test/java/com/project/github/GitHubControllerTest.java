package com.project.github;

import com.project.github.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
//Sadece web (controller) katmanını başlatır.
//        •	Spring’in MVC altyapısını (Spring MVC) test etmek için kullanılır.
//        •	Servis katmanını ve repository katmanını yüklemez.
//        •	Dolayısıyla dış bağımlılıklar (örneğin servisler) @MockBean ile mock (taklit) edilir.
public class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;  //MockMvc nesnesi, web isteklerini gerçek HTTP olmadan simüle edip, controller’ları test etmeye yarar.

    @MockBean
    private GitHubService gitHubService;   //GitHubService gerçek servis yerine mock (taklit) nesne olarak enjekte edilir.

    @Test
    public void testFetchEndpoint_shouldTriggerServiceAndReturnMessage() throws Exception {
        mockMvc.perform(get("/fetch"))
                .andExpect(status().isOk())
                .andExpect(content().string("GitHub data fetched and saved successfully!"));
// 	•	/fetch endpoint’ine GET isteği gönderir.
//	•	Dönen HTTP status kodunun 200 OK olduğunu kontrol eder.
//	•	Dönen içerik (response body) stringinin "GitHub data fetched and saved successfully!" olduğunu doğrular.

        verify(gitHubService).fetchAndSaveData();
//      Mockito’nun verify fonksiyonu ile mocklanan gitHubService içindeki fetchAndSaveData() metodunun çağrılıp çağrılmadığı test edilir.
//		Yani endpoint çağrıldığında servis metodunun tetiklenip tetiklenmediğini doğrular.
    }
}

//kontrolcü (controller) katmanının doğru HTTP yanıtı dönüp dönmediğini ve servis metodunu çağırıp çağırmadığını
//hızlıca doğrulamak için çok kullanışlıdır.