package ua.astound.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.testng.SkipException;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import ua.astound.test.injectModuls.PropertyConfigurationModule;
import ua.astound.test.pageobject.MainHerokuAppPage;
import ua.astound.test.pageobject.frames.IFrame;
import ua.astound.test.utils.ScreenshotMaker;

@Guice(modules = PropertyConfigurationModule.class)
public class UITestsSuit extends FunctionalTest {

    private final String line1;
    private final String line2;

    @Inject
    public UITestsSuit(@Named("dev-data.searchLine1") String line1, @Named("dev-data.searchLine2") String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }

    @Test
    public void task3() {
        MainHerokuAppPage mainHerokuAppPage = new MainHerokuAppPage(driver);
        mainHerokuAppPage.open();
        mainHerokuAppPage.clickOnLInkName("Frames");
        mainHerokuAppPage.clickOnLInkName("iFrame");
        IFrame iframe = mainHerokuAppPage.getFrame();
        iframe.clearTheField().typeIntoTheField(line1).typeIntoTheField(line2);
        iframe.selectTextLines(1);
        iframe.switchToPage();
        mainHerokuAppPage.clickBoldButton();
        new ScreenshotMaker(driver).makeAScreenshot();
        throw new SkipException("This test is done but will be marked as skipped..");
    }
}