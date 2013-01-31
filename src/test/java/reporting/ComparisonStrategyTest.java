package reporting;

import junit.framework.Assert;
import org.junit.Test;

public class ComparisonStrategyTest {

    @Test
    public void testGetComparisonStrategy() {
        String testString = "abcad@1234535";
        Assert.assertEquals(ComparisonStrategy.SUB_IMAGE, ComparisonStrategy.getStrategy(testString));
        testString = "compare: there is an error comparing";
        Assert.assertEquals(ComparisonStrategy.ERROR, ComparisonStrategy.getStrategy(testString));
        testString = "123241";
        Assert.assertEquals(ComparisonStrategy.ONE_TO_ONE, ComparisonStrategy.getStrategy(testString));
    }
}
