package com.example.userOwnReposAPI.controller;

import com.example.userOwnReposAPI.dto.RepoInfo;
import com.example.userOwnReposAPI.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepoInfo>> getUserRepos(@PathVariable String username) {
        log.info("Received request for user repositories: {}", username);
        List<RepoInfo> repos = gitHubService.getUserReposWithBranches(username);
        return ResponseEntity.ok(repos);
    }
}
