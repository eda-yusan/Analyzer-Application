package com.project.github;

import com.project.github.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner {

    @Autowired
    private GitHubService gitHubService;  

    @EventListener(ApplicationReadyEvent.class)  
    public void runAfterStartup() {
        gitHubService.fetchAndSaveData();
    }
}
