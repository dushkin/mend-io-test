package com.ttt.providers;

import com.aventstack.extentreports.ExtentTest;

import io.restassured.response.Response;

public class BitBucketAPIImp implements VersionControlProvider{

    public BitBucketAPIImp(String repoName, ExtentTest test) {
    }

    @Override
    public void createRepo() {
        
        throw new UnsupportedOperationException("Unimplemented method 'createRepo'");
    }

    @Override
    public void deleteRepo() {
        
        throw new UnsupportedOperationException("Unimplemented method 'deleteRepo'");
    }

    @Override
    public void deleteRepo(String repoName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'deleteRepo'");
    }

    @Override
    public void deleteReposByPrefix(String repoNamePrefix) {
        
        throw new UnsupportedOperationException("Unimplemented method 'deleteReposByPrefix'");
    }

    @Override
    public void createBranch(String branchName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'createBranch'");
    }

    @Override
    public void arbitrarilyModifyFileInBranch(String branchName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'arbitrarilyModifyFileInBranch'");
    }

    @Override
    public int createPR(String featureBranch, String baseBranch) {
        
        throw new UnsupportedOperationException("Unimplemented method 'createPR'");
    }

    @Override
    public void mergePR(String userName, String repoName, int prNumber) {
        
        throw new UnsupportedOperationException("Unimplemented method 'mergePR'");
    }

    @Override
    public Response getPullRequestEntity(int prNumber) {
        
        throw new UnsupportedOperationException("Unimplemented method 'getPullRequestEntity'");
    }

    @Override
    public Response validatePRStatus(int prNumber) {
        
        throw new UnsupportedOperationException("Unimplemented method 'validatePRStatus'");
    }

    @Override
    public void validateMergeCommitExists(String mergeCommitSha) {
        
        throw new UnsupportedOperationException("Unimplemented method 'validateMergeCommitExists'");
    }

    @Override
    public int createIssue(String title, String body, String userName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'createIssue'");
    }

    @Override
    public void addIssueLabel(String testLabel, int issueNumber, String userName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'addIssueLabel'");
    }

    @Override
    public void assignIssue(String assignee, int issueNumber, String userName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'assignIssue'");
    }

    @Override
    public void closeIssue(int issueNumber, String userName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'closeIssue'");
    }

    @Override
    public String createProject(String projectName, String projectBody, String userName) {
        
        throw new UnsupportedOperationException("Unimplemented method 'createProject'");
    }

    @Override
    public void addIssueToProject(int issueNumber, String projectId) {
        
        throw new UnsupportedOperationException("Unimplemented method 'addIssueToProject'");
    }
    
}
