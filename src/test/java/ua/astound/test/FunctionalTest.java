package ua.astound.test;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FunctionalTest {

    protected static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        WebDriverManager.chromedriver().version("76").setup();
        driver = new ChromeDriver(new ChromeOptions());
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterTest
    public void cleanUp() {
        try {
            driver.manage().deleteAllCookies();
        } catch (NoSuchSessionException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }
}
