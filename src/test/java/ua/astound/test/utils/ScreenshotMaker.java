package ua.astound.test.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotMaker {

    private WebDriver driver;

    private DateTimeFormatter dtf;
    private LocalDateTime now;

    public ScreenshotMaker(WebDriver driver) {
        this.driver = driver;
    }

    public void makeAScreenshot() {

        dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        now = LocalDateTime.now();

        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(String.format("target/screenshots/screenshot%s.png", dtf.format(now))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
