package com.ttt.tests;
import org.junit.jupiter.api.*;

import com.aventstack.extentreports.Status;

import com.ttt.providers.VersionControlFactory;
import com.ttt.providers.VersionControlProvider;
import com.ttt.providers.VersionControlType;

public class ProjectsTests extends BaseTest {
    private String projectId;
    private String repoName = uniqueRepoName();
    private VersionControlProvider provider;
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);
        
        provider = VersionControlFactory.getVersionControlProvider(VersionControlType.GITHUB, repoName, test);

        projectId = provider.createProject("Test Project", "This is a test project for automation.", gitHubUserName);
    }

    @Test
    void shouldAddIssueToProject() {
        
        test.log(Status.INFO, "Starting test: Add issue to project");
        
        int issueNumber = provider.createIssue("Test Issue", "This is a test issue for the project.", gitHubUserName);
        provider.addIssueToProject(issueNumber, projectId);

        test.log(Status.PASS, "Successfully added issue to project");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        provider.deleteRepo();
    }
}
