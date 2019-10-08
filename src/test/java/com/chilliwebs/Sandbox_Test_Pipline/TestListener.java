package com.chilliwebs.Sandbox_Test_Pipline;

import java.io.FileOutputStream;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class TestListener extends RunListener {

    private void takeScreenshot(String testMethodName) throws Exception {
        if (SimpleFWUpdateTest.driver != null) {
            FileOutputStream imageFileOutputStream = new FileOutputStream("./"+testMethodName+"_" + SimpleFWUpdateTest.machine + "_" + SimpleFWUpdateTest.device + "_" + SimpleFWUpdateTest.browser+".png");
            imageFileOutputStream.write(((TakesScreenshot) SimpleFWUpdateTest.driver).getScreenshotAs(OutputType.BYTES));
            imageFileOutputStream.flush();
            imageFileOutputStream.close();
            System.out.println("Screenshot taken");
        }
    }

    private void stopDriver() {
        if (SimpleFWUpdateTest.driver != null) {
            SimpleFWUpdateTest.driver.quit();
            SimpleFWUpdateTest.driver = null;
        }
    }

    public void testRunStarted(Description description) throws Exception {
        System.out.println("Number of tests to execute: " + description.testCount());
    }

    public void testRunFinished(Result result) throws Exception {
        System.out.println("Number of tests executed: " + result.getRunCount());
        stopDriver();
    }

    public void testStarted(Description description) throws Exception {
        System.out.println("Starting: " + description.getMethodName());
    }

    public void testFinished(Description description) throws Exception {
        System.out.println("Finished: " + description.getMethodName());
        takeScreenshot(description.getMethodName());
    }

    public void testFailure(Failure failure) throws Exception {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
        takeScreenshot(failure.getDescription().getMethodName());
    }

    public void testAssumptionFailure(Failure failure)  {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    public void testIgnored(Description description) throws Exception {
        System.out.println("Ignored: " + description.getMethodName());
    }
}