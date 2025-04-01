package com.ttt.tests;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.jupiter.api.AfterAll;

/**
 * Parallel Test Execution Configuration
 */

@Suite
@SelectPackages("com.ttt.tests")
public class ParallelTestsRunner {
    @AfterAll
    static void tearDown() {
        BaseTest.driver.quit();
    }
}
