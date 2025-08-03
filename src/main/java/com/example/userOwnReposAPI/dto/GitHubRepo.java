package com.example.userOwnReposAPI.dto;

import lombok.Data;

@Data
public class GitHubRepo {
    private String name;
    private boolean fork;
    private Owner owner;
}
