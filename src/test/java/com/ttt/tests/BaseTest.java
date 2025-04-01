package com.ttt.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class BaseTest {
    public static WebDriver driver;
    protected static String gitHubUserName = System.getenv("GH_USERNAME");
    protected static String gitHubToken = System.getenv("GH_TOKEN");
    protected static final String DEFAULT_BASE_BRANCH = "main";
    protected static final AtomicLong idCounter = new AtomicLong();
    protected static JsonFormatter jsonFormatter;
    protected static ExtentReports extent;
    protected static ExtentSparkReporter sparkReporter;
    protected static ExtentTest test;

    @BeforeAll
    public static void setup() throws IOException { //throws IOException if report init fails

        initReport();

        // Create report directory if it doesn't exist
        File reportDir = new File("target/reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Create and configure the reporter
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/reports/test-report.html");
        reporter.config()
                .setTheme(Theme.STANDARD);
        reporter.config()
                .setDocumentTitle("GitHub API Tests");
        reporter.config()
                .setReportName("Test Results");

        extent.attachReporter(reporter);

        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // optional
        driver = new ChromeDriver(options);
    }

    public static void initReport() throws IOException {
        // Create timestamp for unique report name
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        
        // Create HTML reporter with timestamp in filename
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/spark-report-" + timestamp + ".html")
            .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] { 
                    ViewName.DASHBOARD,
                    ViewName.TEST,
                    ViewName.CATEGORY
                })
                .apply();
        
        // Initialize ExtentReports
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive("./extent.json");
        extent.attachReporter(jsonFormatter, sparkReporter);
    }

    protected static String uniqueRepoName() {
        return "test-repo-" +
                Thread.currentThread().threadId() + "-" +
                idCounter.getAndIncrement();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush();
    }
}