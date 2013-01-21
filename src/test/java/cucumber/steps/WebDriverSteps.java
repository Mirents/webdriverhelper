package cucumber.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import helper.WebDriverHelper;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverSteps {
    private WebDriverHelper webDriverHelper;

    @Before
    public void setUp() {
        webDriverHelper = WebDriverHelper.getInstance();
        webDriverHelper.setDriver(new FirefoxDriver());
    }

    @After
    public void tearDown() {
        webDriverHelper.webDriverQuit();
    }
}
