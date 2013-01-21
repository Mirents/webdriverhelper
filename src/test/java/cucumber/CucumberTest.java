package cucumber;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Cucumber.Options(glue = {"cucumber.steps"}, features = {"src/test/resources/simpletest.feature"}, format = {"pretty", "html:target/cucumber-html-report", "json-pretty:target/cucumber-report.json"})
public class CucumberTest {
}
