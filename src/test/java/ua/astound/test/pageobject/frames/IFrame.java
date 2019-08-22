package ua.astound.test.pageobject.frames;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.stream.IntStream;

import ua.astound.test.pageobject.shared.AbstractFrame;

public class IFrame extends AbstractFrame {

    @FindBy(xpath = "//body")
    private WebElement body;

    public IFrame(WebDriver driver) {
        super(driver);
    }

    public IFrame clearTheField() {
        body.clear();
        return this;
    }

    public IFrame typeIntoTheField(String text) {
        body.sendKeys(text);
        body.sendKeys(Keys.ENTER);
        return this;
    }

    public void selectTextLines(int linesCountToBeSelected) {
        Actions actions = new Actions(driver).sendKeys(Keys.PAGE_UP).keyDown(Keys.SHIFT);
        IntStream.range(0, linesCountToBeSelected).mapToObj(i -> Keys.ARROW_DOWN).forEach(actions::sendKeys);
        actions.perform();
    }
}
