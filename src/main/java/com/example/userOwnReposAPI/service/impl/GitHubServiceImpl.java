package com.example.userOwnReposAPI.service.impl;

import com.example.userOwnReposAPI.dto.Branch;
import com.example.userOwnReposAPI.dto.BranchInfo;
import com.example.userOwnReposAPI.dto.GitHubRepo;
import com.example.userOwnReposAPI.dto.RepoInfo;
import com.example.userOwnReposAPI.feignClient.GitHubClient;
import com.example.userOwnReposAPI.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubServiceImpl implements GitHubService {

    private final GitHubClient gitHubClient;

    public List<RepoInfo> getUserReposWithBranches(String username) {
        log.info("Fetching repositories for user: {}", username);

        List<GitHubRepo> repos = gitHubClient.getUserRepos(username);

        List<RepoInfo> result = repos.stream()
                .filter(repo -> !repo.isFork())
                .map(this::mapToRepoInfo)
                .collect(Collectors.toList());

        log.info("Found {} non-fork repositories for user: {}", result.size(), username);
        return result;
    }

    private RepoInfo mapToRepoInfo(GitHubRepo repo) {
        List<Branch> branches = gitHubClient.getBranches(
                repo.getOwner().getLogin(),
                repo.getName()
        );

        List<BranchInfo> branchInfos = branches.stream()
                .map(branch -> new BranchInfo(branch.getName(), branch.getCommit().getSha()))
                .collect(Collectors.toList());

        return new RepoInfo(repo.getName(), repo.getOwner().getLogin(), branchInfos);
    }
}
