package util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CompareUtilTest {

    @Test
    public void testDifferentListSizes() {
        List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> actualList = new ArrayList<Map<String, String>>();
        Map<String, String> actualFirstMap = new HashMap<String, String>();
        Map<String, String> actualSecondMap = new HashMap<String, String>();
        Map<String, String> expectedMap = new HashMap<String, String>();
        actualFirstMap.put("Header 1", "Value 1");
        actualFirstMap.put("Header 2", "Value 2");
        actualSecondMap.put("Header 1", "Value 3");
        actualSecondMap.put("Header 2", "Value 4");
        expectedMap.put("Header 1", "Value 1");
        expectedMap.put("Header 2", "Value 2");
        expectedList.add(expectedMap);
        actualList.add(actualFirstMap);
        actualList.add(actualSecondMap);
        CompareResult compareResult = CompareUtil.compareListOfMaps(expectedList, actualList);
        assertThat(compareResult.getReason(), equalTo("Size of lists do not match, Expected: 1 , but Actual: 2"));
        assertFalse(compareResult.isOutcome());
    }

    @Test
    public void testDifferentHeaderValues() {
        List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> actualList = new ArrayList<Map<String, String>>();
        Map<String, String> actualMap = new HashMap<String, String>();
        Map<String, String> expectedMap = new HashMap<String, String>();
        actualMap.put("Header 1", "Value 1");
        actualMap.put("Header 2", "Value 2");
        expectedMap.put("Header 1", "Value 1");
        expectedMap.put("Header B", "Value 2");
        expectedList.add(expectedMap);
        actualList.add(actualMap);
        CompareResult compareResult = CompareUtil.compareListOfMaps(expectedList, actualList);
        assertThat(compareResult.getReason(), equalTo("No expected header match found for actual header: Header 2"));
        assertFalse(compareResult.isOutcome());
    }

    @Test
    public void testDifferentMapValues() {
        List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> actualList = new ArrayList<Map<String, String>>();
        Map<String, String> actualMap = new HashMap<String, String>();
        Map<String, String> expectedMap = new HashMap<String, String>();
        actualMap.put("Header 1", "Value 1");
        actualMap.put("Header 2", "Value 2");
        expectedMap.put("Header 1", "Value 1");
        expectedMap.put("Header 2", "Value A");
        expectedList.add(expectedMap);
        actualList.add(actualMap);
        CompareResult compareResult = CompareUtil.compareListOfMaps(expectedList, actualList);
        assertThat(compareResult.getReason(), equalTo("Expected column value: Value A at row: 1 " +
                                                     "does not match Actual column value: Value 2"));
        assertFalse(compareResult.isOutcome());
    }

    @Test
    public void testMatchingValues() {
        List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> actualList = new ArrayList<Map<String, String>>();
        Map<String, String> actualMap = new HashMap<String, String>();
        Map<String, String> expectedMap = new HashMap<String, String>();
        actualMap.put("Header 1", "Value 1");
        actualMap.put("Header 2", "Value 2");
        expectedMap.put("Header 1", "Value 1");
        expectedMap.put("Header 2", "Value 2");
        expectedList.add(expectedMap);
        actualList.add(actualMap);
        CompareResult compareResult = CompareUtil.compareListOfMaps(expectedList, actualList);
        assertThat(compareResult.getReason(), equalTo(null));
        assertTrue(compareResult.isOutcome());
    }
}
