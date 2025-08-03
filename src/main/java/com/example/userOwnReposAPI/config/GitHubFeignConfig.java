package com.example.userOwnReposAPI.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Accept", "application/vnd.github+json");
            requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
        };
    }
}
