package example;

import helper.PredicateHelper;
import helper.WebDriverHelper;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import pages.GooglePage;

import java.util.List;


public class SimpleTest {
    private static WebDriverHelper webDriverHelper;

    @BeforeClass
    public static void setUp() throws Exception {
        webDriverHelper = WebDriverHelper.getInstance();
        webDriverHelper.setDriver(new FirefoxDriver());
    }

    @AfterClass
    public static void tearDown() {
        webDriverHelper.webDriverQuit();
    }

    @Test
    public void simpleTest() {
        GooglePage googlePage = PageFactory.initElements(webDriverHelper.getWebDriver(), GooglePage.class);
        googlePage.openHomepage();
        googlePage.searchFor("testing");
        String resultHeading = googlePage.getResultHeadingByIndex(0);
        Assert.assertEquals(true, resultHeading.contains("testing"));
    }
}
