package com.example.userOwnReposAPI.integration;

import com.example.userOwnReposAPI.dto.Branch;
import com.example.userOwnReposAPI.dto.BranchInfo;
import com.example.userOwnReposAPI.dto.GitHubRepo;
import com.example.userOwnReposAPI.dto.RepoInfo;
import com.example.userOwnReposAPI.feignClient.GitHubClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.userOwnReposAPI.dataFactory.BranchFactory.getFirstRepoBranches;
import static com.example.userOwnReposAPI.dataFactory.BranchFactory.getSecondRepoBranches;
import static com.example.userOwnReposAPI.dataFactory.BranchFactory.getThirdRepoBranches;
import static com.example.userOwnReposAPI.dataFactory.GitHubRepoFactory.getGitHubRepos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubUserReposIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GitHubClient gitHubClient;

    @Test
    void shouldReturnUserRepositoriesWithBranchesInHappyPath() {
        // GIVEN
        String username = "testuser";
        List<GitHubRepo> mockRepos = getGitHubRepos();
        List<Branch> firstRepoBranches = getFirstRepoBranches();
        List<Branch> secondRepoBranches = getSecondRepoBranches();
        List<Branch> thirdRepoBranches = getThirdRepoBranches();

        when(gitHubClient.getUserRepos(username)).thenReturn(mockRepos);
        when(gitHubClient.getBranches("testuser", "my-awesome-project")).thenReturn(firstRepoBranches);
        when(gitHubClient.getBranches("testuser", "another-original-repo")).thenReturn(secondRepoBranches);
        when(gitHubClient.getBranches("testuser", "empty-repo")).thenReturn(thirdRepoBranches);

        // WHEN
        ResponseEntity<RepoInfo[]> response = restTemplate.getForEntity(
                "/users/{username}/repos",
                RepoInfo[].class,
                username
        );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<RepoInfo> repos = Arrays.asList(response.getBody());

        assertThat(repos).hasSize(3);
        Set<String> repoNames = repos.stream()
                .map(RepoInfo::getRepositoryName)
                .collect(Collectors.toSet());
        assertThat(repoNames).containsExactlyInAnyOrder(
                "my-awesome-project",
                "another-original-repo",
                "empty-repo"
        );

        Set<String> ownerLogins = repos.stream()
                .map(RepoInfo::getOwnerLogin)
                .collect(Collectors.toSet());
        assertThat(ownerLogins).containsOnly("testuser");

        int totalBranches = repos.stream()
                .mapToInt(repo -> repo.getBranches().size())
                .sum();
        assertThat(totalBranches).isEqualTo(4);

        RepoInfo firstRepo = findRepoByName(repos, "my-awesome-project");
        assertThat(firstRepo.getRepositoryName()).isEqualTo("my-awesome-project");
        assertThat(firstRepo.getOwnerLogin()).isEqualTo("testuser");
        assertThat(firstRepo.getBranches()).hasSize(3);

        validateBranch(firstRepo.getBranches(), "main", "abc123def456ghi789jkl012mno345pqr678stu");
        validateBranch(firstRepo.getBranches(), "develop", "xyz987wvu654tsr321qpo098nml765kji432hgf");
        validateBranch(firstRepo.getBranches(), "feature/new-functionality", "123abc456def789ghi012jkl345mno678pqr901");

        List<String> allCommitShas = repos.stream()
                .flatMap(repo -> repo.getBranches().stream())
                .map(BranchInfo::getLastCommitSha)
                .collect(Collectors.toList());

        assertThat(allCommitShas).hasSize(4);
        assertThat(allCommitShas).allSatisfy(sha -> assertThat(sha).isNotNull());

        verify(gitHubClient, times(1)).getUserRepos(username);
        verify(gitHubClient, times(3)).getBranches(eq("testuser"), anyString());

        verify(gitHubClient, never()).getBranches("originalowner", "forked-repository");
        verifyNoMoreInteractions(gitHubClient);
    }

    private RepoInfo findRepoByName(List<RepoInfo> repos, String name) {
        return repos.stream()
                .filter(repo -> name.equals(repo.getRepositoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Repository '" + name + "' not found"));
    }

    private void validateBranch(List<BranchInfo> branches, String expectedName, String expectedSha) {
        BranchInfo branch = branches.stream()
                .filter(b -> expectedName.equals(b.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Branch '" + expectedName + "' not found"));

        assertThat(branch.getName()).isEqualTo(expectedName);
        assertThat(branch.getLastCommitSha()).isEqualTo(expectedSha);
    }
}
