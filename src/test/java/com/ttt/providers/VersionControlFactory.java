package com.ttt.providers;

import com.aventstack.extentreports.ExtentTest;

public class VersionControlFactory {

    public static VersionControlProvider getVersionControlProvider(VersionControlType type, final String repoName, ExtentTest test) {
        switch (type) {
            case GITHUB:
                return new GitHubAPIImp(repoName, test);
            case BITBUCKET:
                return new BitBucketAPIImp(repoName, test);
            default:
                throw new IllegalArgumentException("Unsupported version control type: " + type);
        }
    }
}