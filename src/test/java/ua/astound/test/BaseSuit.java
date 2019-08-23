package ua.astound.test;

import org.testng.annotations.BeforeSuite;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BaseSuit {

    @BeforeSuite
    public void setStartSuiteTimeToSystemVariable() {
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.setProperty("startSuiteTime", sdf.format(currentTime));
    }
}
