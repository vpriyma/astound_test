package ua.astound.test;

import org.testng.annotations.Test;

import ua.astound.test.pageobject.MainHerokuAppPage;
import ua.astound.test.pageobject.frames.IFrame;
import ua.astound.test.utils.PropertyReader;
import ua.astound.test.utils.ScreenshotMaker;

public class UITestsSuit extends FunctionalTest{

    @Test
    public void task3() {
        MainHerokuAppPage mainHerokuAppPage = new MainHerokuAppPage(driver);
        mainHerokuAppPage.open();
        mainHerokuAppPage.clickOnLInkName("Frames");
        mainHerokuAppPage.clickOnLInkName("iFrame");
        IFrame iframe = mainHerokuAppPage.getFrame();
        iframe.clearTheField()
                .typeIntoTheField(PropertyReader.INSTANCE.getPropertyValue("searchLine1"))
                .typeIntoTheField(PropertyReader.INSTANCE.getPropertyValue("searchLine2"));
        iframe.selectTextLines(1);
        iframe.switchToPage();
        mainHerokuAppPage.clickBoldButton();
        new ScreenshotMaker(driver).makeAScreenshot();
    }
}