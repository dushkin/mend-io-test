package com.ttt.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.Status;

import com.ttt.providers.VersionControlFactory;
import com.ttt.providers.VersionControlProvider;
import com.ttt.providers.VersionControlType;

public class RepositoryTests extends BaseTest {
    
    private VersionControlProvider provider;
    private String repoName = uniqueRepoName();
    
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        
        String testName = testInfo.getDisplayName();
        test = extent.createTest(testName);
        
        provider = VersionControlFactory.getVersionControlProvider(VersionControlType.GITHUB, repoName, test);
    }

    @Test
    void shouldCreateNewRepository() {

        test.log(Status.INFO, "Starting repository creation test");
        
        provider.createRepo();
        
        driver.get("https://github.com/" + System.getenv("GH_USERNAME"));
        test.log(Status.INFO, "Navigated to GitHub profile page");
        
        WebElement repoLink = driver.findElement(By.linkText(repoName));
        assertTrue(repoLink.isDisplayed());

        test.log(Status.PASS, "Repository found and verified in UI");
    }

    @AfterEach
    void afterEach() {
        test.log(Status.INFO, "Cleaning up test resources");
        provider.deleteRepo();
    }
}