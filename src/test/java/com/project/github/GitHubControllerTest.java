package com.project.github;

import com.project.github.dto.RepositoryDTO;
import com.project.github.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Test
    public void testFetchEndpoint_shouldTriggerServiceAndReturnMap() throws Exception {
        // Mock servisin dönüş değerini hazırla
        List<RepositoryDTO> mockRepos = Collections.emptyList();
        when(gitHubService.fetchAndSaveData()).thenReturn(Map.of("repositories", mockRepos));

        mockMvc.perform(get("/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());  // Root array olduğu için $.repositories yok

        verify(gitHubService).fetchAndSaveData();
    }
}