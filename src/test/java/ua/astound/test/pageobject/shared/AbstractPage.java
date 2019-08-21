package ua.astound.test.pageobject.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class BasePage extends PageObject {

    private static final String BASE_LINK = "//a[text()='%s']";

    public BasePage(WebDriver driver) {
        super(driver);
    }

    public void clickOnLInkName(String link) {
        driver.findElement(By.xpath(String.format(BASE_LINK, link))).click();
    }

    public void getPage() {
        driver.switchTo().defaultContent();
    }
}
