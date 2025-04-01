package com.ttt.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import com.ttt.providers.GitHubAPIImp;
import com.aventstack.extentreports.Status;

public class PullRequestTests extends BaseTest {
    private String featureBranch;
    private String repoName = uniqueRepoName();
    private GitHubAPIImp gitHubAPI;
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);

        gitHubAPI = new GitHubAPIImp(repoName, test);

        featureBranch = "feature-" + System.currentTimeMillis();

        test.log(Status.INFO, "Starting test setup for: " + testName);

        gitHubAPI.createRepo();
        gitHubAPI.createBranch(featureBranch);
        gitHubAPI.arbitrarilyModifyFileInBranch(featureBranch);
    }

    @Test
    @Order(1)
    void shouldCreateValidPullRequest() {

        test.log(Status.INFO, "Attempting to create pull request from " + featureBranch + " to " + DEFAULT_BASE_BRANCH);
        gitHubAPI.createPR(featureBranch, DEFAULT_BASE_BRANCH);
        test.log(Status.PASS, "Valid PR creation successful");
    }

    @Test
    @Order(2)
    void shouldMergePullRequest() {
        
        test.log(Status.INFO, "Starting pull request merge process");

        int prNumber = gitHubAPI.createPR(featureBranch, DEFAULT_BASE_BRANCH);
        gitHubAPI.mergePR(gitHubUserName, repoName, prNumber);
        
        Response prResponse = gitHubAPI.validatePRStatus(prNumber);
        
        String mergeCommitSha = prResponse.jsonPath().getString("merge_commit_sha");
        test.log(Status.INFO, "Retrieved merge commit SHA: " + mergeCommitSha);
        
        gitHubAPI.validateMergeCommitExists(mergeCommitSha);
        test.log(Status.PASS, "Merge commit validation successful");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        gitHubAPI.deleteRepo();
    }
}