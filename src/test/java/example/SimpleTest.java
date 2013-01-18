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
        webDriverHelper.openUrl("http://www.google.com");
        webDriverHelper.enterTextInput(webDriverHelper.findElement(By.id("gbqfq")), "testing");
        WebElement searchButton = webDriverHelper.findElement(By.id("gbqfb"));
        searchButton.click();
        WebElement resultsList = webDriverHelper.findElement(By.id("rso"));
        List<WebElement> individualResults = resultsList.findElements(By.cssSelector("li"));
        WebElement firstResult = individualResults.get(0);
        WebElement resultHeading = firstResult.findElement(By.cssSelector("h3"));
        webDriverHelper.webDriverWait(10, PredicateHelper.elementContainsText(resultHeading, "testing"));
        Assert.assertEquals(true, resultHeading.getText().contains("testing"));
    }
}
