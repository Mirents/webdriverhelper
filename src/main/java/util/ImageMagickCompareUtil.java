package util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.apache.commons.io.comparator.NameFileComparator;

import reporting.CSVReportBuilder;
import reporting.ComparisonStrategy;
import reporting.ResultRow;

public class ImageMagickCompareUtil {
    private final String ACTUAL_SCREENS_PATH = "screenshot/actual/";
    private final String EXPECTED_SCREENS_PATH = "screenshot/expected/";
    private final String DIFF_SCREENS_PATH = "screenshot/diff/";
    private final String PATH_TO_IM_BINARY = "/opt/local/bin/compare";
    private final String RESULTS_FILE_PATH = "screenshot/results.csv";
    private CSVReportBuilder csvReportBuilder = new CSVReportBuilder(RESULTS_FILE_PATH);

    private File[] getActualScreenshotFiles() {
        return new File(ACTUAL_SCREENS_PATH).listFiles();
    }

    private File[] getExpectedScreenshotFiles() {
        return new File(EXPECTED_SCREENS_PATH).listFiles();
    }

    public void compareAndCaptureResults() {
        File[] actualFiles = getActualScreenshotFiles();
        Arrays.sort(actualFiles, NameFileComparator.NAME_COMPARATOR);
        File[] expectedFiles = getExpectedScreenshotFiles();
        Arrays.sort(expectedFiles, NameFileComparator.NAME_COMPARATOR);
        try {
            for(int i = 0; i < expectedFiles.length; i++) {
                Image expectedImage = new Image(EXPECTED_SCREENS_PATH + expectedFiles[i].getName());
                Image actualImage = new Image(ACTUAL_SCREENS_PATH + actualFiles[i].getName());
                CommandBuilder commandBuilder = buildCommand(actualFiles[i], expectedFiles[i], expectedImage, actualImage);
                String commandOutput = executeCommandAndGetOutput(commandBuilder.getCommandAsArray());
                ResultRow resultRow = getResultRow(actualFiles[i], expectedFiles[i],
                                                    expectedImage, actualImage,
                                                    commandBuilder, commandOutput);
                csvReportBuilder.addColumnValues(resultRow.getResultsAsMap());
            }
            csvReportBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ResultRow getResultRow(File actualFile, File expectedFile, Image expectedImage, Image actualImage,
                                   CommandBuilder commandBuilder, String commandOutput) throws IOException {
        ResultRow resultRow = new ResultRow();
        resultRow.setExpectedTotalPixels(expectedImage.getTotalPixels());
        resultRow.setActualTotalPixels(actualImage.getTotalPixels());
        resultRow.setExpectedFileName(expectedFile.getName());
        resultRow.setActualFileName(actualFile.getName());
        resultRow.setCommandExecuted(commandBuilder.toString());
        resultRow.setStrategyUsed(ComparisonStrategy.getStrategy(commandOutput));
        captureAndStoreResults(resultRow, commandOutput);
        return resultRow;
    }

    private CommandBuilder buildCommand(File actualFile, File expectedFile, Image expectedImage, Image actualImage) {
        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.setPathToImageMagickBinary(PATH_TO_IM_BINARY);
        commandBuilder.setFirstImagePixels(expectedImage.getTotalPixels());
        commandBuilder.setSecondImagePixels(actualImage.getTotalPixels());
        commandBuilder.setFilePaths(EXPECTED_SCREENS_PATH + expectedFile.getName(),
                ACTUAL_SCREENS_PATH + actualFile.getName(),
                DIFF_SCREENS_PATH + expectedFile.getName());
        commandBuilder.build();
        return commandBuilder;
    }

	private String executeCommandAndGetOutput(String[] command) throws IOException,
			InterruptedException {
		Process process = Runtime.getRuntime().exec(command);
		StreamGobbler errorGobbler = gobbleStream(process);
		System.out.println("Exit Value: " + process.waitFor());
        return errorGobbler.getOutputLine();
	}

    private void captureAndStoreResults(ResultRow resultRow, String output) throws IOException {
        output = getFinalOutput(resultRow.getStrategyUsed(), output);
        BigDecimal percentageDeviation;
        if(resultRow.getExpectedTotalPixels().longValue() > resultRow.getActualTotalPixels().longValue()) {
        	percentageDeviation = calculatePercentageDeviation(resultRow.getExpectedTotalPixels(), output);
        } else {
        	percentageDeviation = calculatePercentageDeviation(resultRow.getActualTotalPixels(), output); 
        }
        resultRow.setPercentageDeviation(percentageDeviation);
        resultRow.setOutput(output);
    }

    private String getFinalOutput(ComparisonStrategy strategy, String output) {
        if(strategy.equals(ComparisonStrategy.SUB_IMAGE)) {
            output = output.replace(output.substring(output.indexOf("@"), output.length()), "").trim();
        }
        return output;
    }

    private BigDecimal calculatePercentageDeviation(BigDecimal totalImagePixels, String output) throws IOException {
        BigDecimal percentageDeviation;
        try {
            BigDecimal pixelDifference = new BigDecimal(output);
            percentageDeviation = pixelDifference.divide(totalImagePixels, 4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            percentageDeviation = BigDecimal.valueOf(-1);
        }
        return percentageDeviation.multiply(new BigDecimal(100));
    }

    private StreamGobbler gobbleStream(Process process) {
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error Stream");
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Input Stream");
        errorGobbler.start();
        outputGobbler.start();
        return errorGobbler;
    }

    public static void main(String[] args) {
        new ImageMagickCompareUtil().compareAndCaptureResults();
    }
}
