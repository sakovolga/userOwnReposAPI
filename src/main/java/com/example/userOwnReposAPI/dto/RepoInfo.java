package com.example.userOwnReposAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RepoInfo {
    private String repositoryName;
    private String ownerLogin;
    private List<BranchInfo> branches;
}
