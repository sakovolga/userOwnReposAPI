package com.example.userOwnReposAPI.service;

import com.example.userOwnReposAPI.dto.RepoInfo;

import java.util.List;

public interface GitHubService {
    List<RepoInfo> getUserReposWithBranches(String username);
}
