package util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareUtil {

    public static CompareResult compareListOfMaps(List<Map<String, String>> expectedList, List<Map<String, String>> actualList) {
        CompareResult result = new CompareResult();
        if(expectedList.size() != actualList.size()) {
            result.setOutcome(false);
            result.setReason("Size of lists do not match, Expected: " + expectedList.size() + " , but Actual: " + actualList.size());
            return result;
        }
        Set<String> actualHeaders = actualList.get(0).keySet();
        Set<String> expectedHeaders = expectedList.get(0).keySet();
        for(String key : actualHeaders) {
            if(!expectedHeaders.contains(key)) {
                result.setOutcome(false);
                result.setReason("No expected header match found for actual header: " + key);
                return result;
            }
        }
        for(int i = 0; i < expectedList.size(); i++) {
            Map<String, String> expectedRow = expectedList.get(i);
            for(String header : expectedHeaders) {
                Map<String, String> actualRow = actualList.get(i);
                if(!expectedRow.get(header).equals(actualRow.get(header))) {
                    result.setOutcome(false);
                    result.setReason("Expected column value: " + expectedRow.get(header) + " at row: " + (i+1) +
                                     "does not match Actual column value: " + actualRow.get(header));
                    return result;
                }
            }
        }
        result.setOutcome(true);
        return result;
    }
}
