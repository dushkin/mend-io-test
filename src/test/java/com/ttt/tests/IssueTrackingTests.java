package com.ttt.tests;
import org.junit.jupiter.api.*;

import com.aventstack.extentreports.Status;

import com.ttt.providers.VersionControlFactory;
import com.ttt.providers.VersionControlProvider;
import com.ttt.providers.VersionControlType;

public class IssueTrackingTests extends BaseTest {
    private static final String issue_title = "Test Issue";
    private static final String issue_body = "Sample issue description";
    private final String testLabel = "bug";
    private int issueNumber;
    private String repoName = uniqueRepoName();
    private VersionControlProvider provider;
    protected static String assignee = System.getenv("GH_USERNAME");
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);

        provider = VersionControlFactory.getVersionControlProvider(VersionControlType.GITHUB, repoName, test);

        test.log(Status.INFO, "Starting test setup for: " + testName);

        provider.createRepo();
        // Create initial issue
        issueNumber = provider.createIssue(issue_title, issue_body, gitHubUserName);
    }

    @Test
    @Order(1)
    void shouldCreateIssueWithLabel() {

        test.log(Status.INFO, "Attempting to add label to issue #" + issueNumber);

        provider.addIssueLabel(testLabel, issueNumber, gitHubUserName);
        
        test.log(Status.PASS, "Successfully added label '" + testLabel + "' to issue");
    }

    @Test
    @Order(2)
    void shouldAssignIssue() {

        test.log(Status.INFO, "Attempting to assign issue #" + issueNumber + " to user: " + assignee);
        
        provider.assignIssue(assignee, issueNumber, gitHubUserName);
        
        test.log(Status.PASS, "Successfully assigned issue to user: " + assignee);
    }

    @Test
    @Order(3)
    void shouldCloseIssue() {
        
        test.log(Status.INFO, "Attempting to close issue #" + issueNumber);
        
        provider.closeIssue(issueNumber, gitHubUserName);
        
        test.log(Status.PASS, "Successfully closed issue");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        provider.deleteRepo();
    }
}