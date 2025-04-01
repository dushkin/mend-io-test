package com.ttt.providers;

import com.aventstack.extentreports.ExtentTest;
import com.ttt.exceptions.VersionControlProviderException;

import io.restassured.response.Response;

public interface VersionControlProvider {

    default void validateResponse(ExtentTest test, Response response, int expectedStatusCode, String operation) {
        
        if (response.getStatusCode() != expectedStatusCode) {
            String errorDetails = String.format(
                "%s failed. Expected status: %d, Actual status: %d. Response: %s",
                operation, expectedStatusCode, response.getStatusCode(), response.getBody().asPrettyString()
            );
            throw new VersionControlProviderException(test, errorDetails);
        }
    }

    //Repository:
    //-----------
    
    void createRepo();
    void deleteRepo();
    void deleteRepo(final String repoName);
    void deleteReposByPrefix(final String repoNamePrefix);// Utility method:

    //Branch:
    //-------

    void createBranch(String branchName);
    void arbitrarilyModifyFileInBranch(String branchName);

    //Pull Requests:
    //--------------
    
    int createPR(final String featureBranch, final String baseBranch);    // returns pr number
    void mergePR(final String userName, final String repoName, final int prNumber);
    Response getPullRequestEntity(final int prNumber);
    Response validatePRStatus(final int prNumber);
    void validateMergeCommitExists(final String mergeCommitSha);

    //Issues:
    //-------

    int createIssue(final String title, final String body, final String userName); // returns issue number
    void addIssueLabel(final String testLabel, final int issueNumber, final String userName);
    void assignIssue(final String assignee, final int issueNumber, final String userName);
    void closeIssue(final int issueNumber, final String userName);

    //Projects:
    //---------

    String createProject(final String projectName, final String projectBody, final String userName);// returns project id
    void addIssueToProject(final int issueNumber, final String projectId);

}
