package com.galapea.techblog.jobboardgriddbcloud.config;

import java.util.Set;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.SessionTrackingMode;

@Configuration
public class ServletConfig {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        // don't append the session id to resources
        return servletContext ->
                servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
    }
}
