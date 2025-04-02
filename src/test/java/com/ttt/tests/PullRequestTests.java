package com.ttt.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import com.aventstack.extentreports.Status;

import com.ttt.providers.VersionControlFactory;
import com.ttt.providers.VersionControlProvider;
import com.ttt.providers.VersionControlType;

public class PullRequestTests extends BaseTest {
    private String featureBranch;
    private String repoName = uniqueRepoName();
    private VersionControlProvider provider;
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);

        provider = VersionControlFactory.getVersionControlProvider(VersionControlType.GITHUB, repoName, test);

        featureBranch = "feature-" + System.currentTimeMillis();

        test.log(Status.INFO, "Starting test setup for: " + testName);

        provider.createRepo();
        provider.createBranch(featureBranch);
        provider.arbitrarilyModifyFileInBranch(featureBranch);
    }

    @Test
    @Order(1)
    void shouldCreateValidPullRequest() {

        test.log(Status.INFO, "Attempting to create pull request from " + featureBranch + " to " + DEFAULT_BASE_BRANCH);
        provider.createPR(featureBranch, DEFAULT_BASE_BRANCH);
        test.log(Status.PASS, "Valid PR creation successful");
    }

    @Test
    @Order(2)
    void shouldMergePullRequest() {
        
        test.log(Status.INFO, "Starting pull request merge process");

        int prNumber = provider.createPR(featureBranch, DEFAULT_BASE_BRANCH);
        provider.mergePR(gitHubUserName, repoName, prNumber);
        
        Response prResponse = provider.validatePRStatus(prNumber);
        
        String mergeCommitSha = prResponse.jsonPath().getString("merge_commit_sha");
        test.log(Status.INFO, "Retrieved merge commit SHA: " + mergeCommitSha);
        
        provider.validateMergeCommitExists(mergeCommitSha);
        test.log(Status.PASS, "Merge commit validation successful");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        provider.deleteRepo();
    }
}