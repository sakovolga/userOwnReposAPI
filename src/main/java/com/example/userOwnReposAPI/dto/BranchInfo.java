package com.example.userOwnReposAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BranchInfo {
    private String name;
    private String lastCommitSha;
}
