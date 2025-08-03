package com.example.userOwnReposAPI.feignClient;

import com.example.userOwnReposAPI.config.GitHubFeignConfig;
import com.example.userOwnReposAPI.dto.Branch;
import com.example.userOwnReposAPI.dto.GitHubRepo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "github-client",
        url = "https://api.github.com",
        configuration = GitHubFeignConfig.class
)
public interface GitHubClient {
    @GetMapping("/users/{username}/repos?type=public")
    List<GitHubRepo> getUserRepos(@PathVariable String username);

    @GetMapping("/repos/{owner}/{repo}/branches")
    List<Branch> getBranches(
            @PathVariable String owner,
            @PathVariable String repo
    );
}
