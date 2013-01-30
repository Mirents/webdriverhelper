package reporting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResultRow {
    private String expectedFileName;
    private String actualFileName;
    private BigDecimal expectedTotalPixels;
    private BigDecimal actualTotalPixels;
    private BigDecimal percentageDeviation;
    private String output;
    private ComparisonStrategy strategyUsed;
    private String notes;
    private String commandExecuted;

    public BigDecimal getExpectedTotalPixels() {
        return expectedTotalPixels;
    }

    public List<String> getResultsAsList() {
        List<String> resultList = new ArrayList<String>();
        resultList.add(expectedFileName);
        resultList.add(actualFileName);
        resultList.add(expectedTotalPixels.toString());
        resultList.add(actualTotalPixels.toString());
        resultList.add(output);
        resultList.add(percentageDeviation.toString());
        resultList.add(strategyUsed.getValue());
        resultList.add(notes);
        resultList.add(commandExecuted);
        return resultList;
    }

    public void setExpectedTotalPixels(BigDecimal expectedTotalPixels) {
        this.expectedTotalPixels = expectedTotalPixels;
    }

    public BigDecimal getActualTotalPixels() {
        return actualTotalPixels;
    }

    public void setActualTotalPixels(BigDecimal actualTotalPixels) {
        this.actualTotalPixels = actualTotalPixels;
    }

    public BigDecimal getPercentageDeviation() {
        return percentageDeviation;
    }

    public void setPercentageDeviation(BigDecimal percentageDeviation) {
        this.percentageDeviation = percentageDeviation;
    }

    public String getExpectedFileName() {
        return expectedFileName;
    }

    public void setExpectedFileName(String expectedFileName) {
        this.expectedFileName = expectedFileName;
    }

    public String getActualFileName() {
        return actualFileName;
    }

    public void setActualFileName(String actualFileName) {
        this.actualFileName = actualFileName;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public ComparisonStrategy getStrategyUsed() {
        return strategyUsed;
    }

    public void setStrategyUsed(ComparisonStrategy strategyUsed) {
        this.strategyUsed = strategyUsed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCommandExecuted() {
        return commandExecuted;
    }

    public void setCommandExecuted(String commandExecuted) {
        this.commandExecuted = commandExecuted;
    }
}
