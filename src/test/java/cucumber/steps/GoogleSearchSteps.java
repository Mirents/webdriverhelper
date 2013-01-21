package cucumber.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import helper.WebDriverHelper;
import junit.framework.Assert;
import org.openqa.selenium.support.PageFactory;
import pages.GooglePage;

public class GoogleSearchSteps {
    private GooglePage googlePage;

    @Given("I open Google Search homepage")
    public void openGoogleSearchHomePage() {
        googlePage = PageFactory.initElements(WebDriverHelper.getInstance().getWebDriver(), GooglePage.class);
        googlePage.openHomepage();
    }

    @When("I search for \"([^\"]*)\"")
    public void searchFor(String searchTerm) {
        googlePage.searchFor(searchTerm);
    }

    @Then("the first heading of the search results contains the word \"([^\"]*)\"")
    public void checkSearchResult(String word) {
        String firstResultHeading = googlePage.getResultHeadingByIndex(0);
        Assert.assertTrue(firstResultHeading.contains(word));
    }
}
