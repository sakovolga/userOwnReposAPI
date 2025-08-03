package com.example.userOwnReposAPI.dataFactory;

import com.example.userOwnReposAPI.dto.Branch;
import com.example.userOwnReposAPI.dto.Commit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BranchFactory {

    public static List<Branch> getFirstRepoBranches(){
        return Arrays.asList(
                createBranch("main", "abc123def456ghi789jkl012mno345pqr678stu"),
                createBranch("develop", "xyz987wvu654tsr321qpo098nml765kji432hgf"),
                createBranch("feature/new-functionality", "123abc456def789ghi012jkl345mno678pqr901")
        );
    }

    public static List<Branch> getSecondRepoBranches(){
        return List.of(
                createBranch("master", "fedcba987654321098765432109876543210abcd")
        );
    }

    public static List<Branch> getThirdRepoBranches(){
        return Collections.emptyList();
    }


    private static Branch createBranch(String name, String commitSha) {
        Branch branch = new Branch();
        branch.setName(name);

        Commit commit = new Commit();
        commit.setSha(commitSha);
        branch.setCommit(commit);

        return branch;
    }
}
