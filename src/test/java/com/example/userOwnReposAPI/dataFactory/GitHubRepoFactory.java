package com.example.userOwnReposAPI.dataFactory;

import com.example.userOwnReposAPI.dto.GitHubRepo;
import com.example.userOwnReposAPI.dto.Owner;

import java.util.Arrays;
import java.util.List;

public class GitHubRepoFactory {

    public static List<GitHubRepo> getGitHubRepos() {
        return Arrays.asList(
                getGitHubRepo("my-awesome-project", false, "testuser"),
                getGitHubRepo("forked-repository", true, "originalowner"),
                getGitHubRepo("another-original-repo", false, "testuser"),
                getGitHubRepo("empty-repo", false, "testuser"));
    }

    private static GitHubRepo getGitHubRepo(String name, boolean fork, String ownerLogin) {
        GitHubRepo repo = new GitHubRepo();
        repo.setName(name);
        repo.setFork(fork);

        Owner owner = new Owner();
        owner.setLogin(ownerLogin);
        repo.setOwner(owner);

        return repo;
    }
}
