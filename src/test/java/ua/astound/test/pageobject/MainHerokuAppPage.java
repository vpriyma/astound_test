package ua.astound.test.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ua.astound.test.pageobject.frames.IFrame;
import ua.astound.test.pageobject.shared.AbstractPage;
import ua.astound.test.utils.PropertyReader;

public class MainHerokuAppPage extends AbstractPage {

    @FindBy(xpath = "//iFrame[@id='mce_0_ifr']")
    private WebElement baseFrame;

    @FindBy(xpath = "//i[@class='mce-ico mce-i-bold']")
    private WebElement boldButton;

    public MainHerokuAppPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(PropertyReader.INSTANCE.getPropertyValue("baseHerokuAppUrl"));
    }

    public IFrame getFrame() {
        driver.switchTo().frame(baseFrame);
        return new IFrame(driver);
    }

    public void clickBoldButton() {
        boldButton.click();
    }
}
