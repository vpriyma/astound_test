package ua.astound.test.utils;

import com.google.gson.Gson;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.astound.test.data.ReportObject;

public class CustomReporter implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String s) {
        ReportObject reportObject = new ReportObject();
        reportObject.setStartTime(System.getProperty("startSuiteTime"));
        List<ReportObject.Test> tests = new ArrayList<>();
        //Iterating over each suite included in the test
        suites.stream().map(ISuite::getResults).forEach(
                suiteResults -> suiteResults.values().stream().map(ISuiteResult::getTestContext).forEach(tc -> {
                    tc.getPassedTests().getAllResults().stream().map(this::getTestObjectFromITestResult)
                            .forEach(tests::add);
                    tc.getFailedTests().getAllResults().stream().map(this::getTestObjectFromITestResult)
                            .forEach(tests::add);
                    tc.getSkippedTests().getAllResults().stream().map(this::getTestObjectFromITestResult)
                            .forEach(tests::add);
                }));
        reportObject.setTests(tests);
        FileWriter fw;
        try {
            fw = new FileWriter("target/report.json");
            fw.write(new Gson().toJson(reportObject));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ReportObject.Test getTestObjectFromITestResult(ITestResult result) {
        ReportObject.Test test = new ReportObject.Test();
        test.setName(result.getName());
        test.setDescription(result.getMethod().getDescription());
        test.setStatus(getStringStatus(result.getStatus()));
        test.setException(result.getThrowable() == null ? "Test Passed" : result.getThrowable().getMessage());
        return test;
    }

    private String getStringStatus(int status) {
        switch (status) {
            case 1:
                return "Passed";
            case 2:
                return "Failed";
            case 3:
                return "Skipped";
            default:
                throw new IllegalArgumentException(String.format("There is no status for [%s] value", status));
        }
    }
}
