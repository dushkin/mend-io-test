package com.ttt.tests;
import org.junit.jupiter.api.*;

import com.aventstack.extentreports.Status;
import com.ttt.providers.GitHubAPIImp;

public class ProjectsTests extends BaseTest {
    private String projectId;
    private String repoName = uniqueRepoName();
    private GitHubAPIImp gitHubAPI;
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);
        
        gitHubAPI = new GitHubAPIImp(repoName, test);

        projectId = gitHubAPI.createProject("Test Project", "This is a test project for automation.", gitHubUserName);
    }

    @Test
    void shouldAddIssueToProject() {
        
        test.log(Status.INFO, "Starting test: Add issue to project");
        
        int issueNumber = gitHubAPI.createIssue("Test Issue", "This is a test issue for the project.", gitHubUserName);
        gitHubAPI.addIssueToProject(issueNumber, projectId);

        test.log(Status.PASS, "Successfully added issue to project");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        gitHubAPI.deleteRepo();
    }
}
