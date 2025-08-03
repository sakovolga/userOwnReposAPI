package com.example.userOwnReposAPI.dto;

import lombok.Data;

@Data
public class Branch {
    private String name;
    private Commit commit;
}
