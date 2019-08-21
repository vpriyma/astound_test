package ua.astound.test.pageobject.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractPage extends PageObject {

    private static final String BASE_LINK = "//a[text()='%s']";

    public AbstractPage(WebDriver driver) {
        super(driver);
    }

    public void clickOnLInkName(String link) {
        driver.findElement(By.xpath(String.format(BASE_LINK, link))).click();
    }

    public void switchToPage() {
        driver.switchTo().defaultContent();
    }
}
