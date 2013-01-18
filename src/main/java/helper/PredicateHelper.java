package helper;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PredicateHelper {
    public static Predicate<WebDriver> elementIsDisplayed(final WebElement element) {
        return new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
                return element.isDisplayed();
            }
        };
    }

    public static Predicate<WebDriver> elementIsNotDisplayed(final WebElement element) {
        return new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return !element.isDisplayed();
            }
        };
    }

    public static Predicate<WebDriver> elementContainsText(final WebElement element, final String value) {
        return new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return element.getText().contains(value);
            }
        };
    }
}
