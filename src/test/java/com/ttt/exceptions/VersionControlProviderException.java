package com.ttt.exceptions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class VersionControlProviderException extends RuntimeException {
    public VersionControlProviderException(ExtentTest test, String message) {
        super(message);
        test.log(Status.FAIL, message);
    }
}