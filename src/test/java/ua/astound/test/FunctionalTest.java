package ua.astound.test;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

public class FunctionalTest extends BaseSuit {

    protected static RemoteWebDriver driver;

    @BeforeClass
    public static void setUp() throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities().chrome();
        dc.setBrowserName("chrome");
        dc.setVersion("76");
        dc.setCapability("chrome.switches", Arrays.asList("--disable-extensions"));
        dc.setCapability("enableVNC", true);
        dc.setCapability("enableVideo", false);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--disable-save-password-bubble");
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        dc.setJavascriptEnabled(true);

        driver = new RemoteWebDriver(URI.create("http://192.168.99.100:4444/wd/hub").toURL(), dc);
        driver.manage().window().setSize(new Dimension(1920, 1080));

        /*   Simple method
        WebDriverManager.chromedriver().version("76").setup();
        driver = new ChromeDriver(new ChromeOptions());
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        */

    }

    @AfterTest
    public void cleanUp() {
        try {
            driver.manage().deleteAllCookies();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }
}
