package example;

import helper.WebDriverHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ScreenshotTest {
    private static WebDriverHelper webDriverHelper;

    @BeforeClass
    public static void setUp() throws Exception {
        webDriverHelper = WebDriverHelper.getInstance();
        webDriverHelper.setDriver(new FirefoxDriver());
        webDriverHelper.maximiseWindow();
    }

    @AfterClass
    public static void tearDown() {
        webDriverHelper.webDriverQuit();
    }

    @Test
    public void simpleTest() {
        webDriverHelper.openUrl("http://www.facebook.com");
        webDriverHelper.takeScreenshot("screenshot/actual/actual_fbMain.png");
        webDriverHelper.openUrl("http://www.facebook.com/help/?ref=pf");
        webDriverHelper.takeScreenshot("screenshot/actual/actual_fbHelp.png");
    }
}
