package ua.astound.test.pageobject.shared;

import org.openqa.selenium.WebDriver;

public abstract class AbstractFrame extends PageObject {

    public AbstractFrame(WebDriver driver) {
        super(driver);
    }

    public void switchToPage() {
        driver.switchTo().defaultContent();
    }
}
