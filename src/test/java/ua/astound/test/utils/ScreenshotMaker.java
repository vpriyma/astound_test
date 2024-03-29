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
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(String.format("target/screenshots/screenshot%s.png",
                                                               LocalDateTime.now().toString().replaceAll(":", "_"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
