package util;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.lang3.StringUtils;
import reporting.CSVReportBuilder;
import reporting.ComparisonStrategy;
import reporting.ResultRow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageMagickCompareUtil {
    private final String ACTUAL_SCREENSHOT_FILE_PREFIX = "actual_";
    private final String EXPECTED_SCREENSHOT_FILE_PREFIX = "expected_";
    private final String ACTUAL_SCREENS_PATH = "screenshot/actual/";
    private final String EXPECTED_SCREENS_PATH = "screenshot/expected/";
    private final String DIFF_SCREENS_PATH = "screenshot/diff/";
    private final String PATH_TO_IM_BINARY = "/opt/local/bin/compare";
    private final String RESULTS_FILE_PATH = "screenshot/results.csv";
    private CSVReportBuilder csvReportBuilder = new CSVReportBuilder(RESULTS_FILE_PATH);

    private final String METRIC_PARAMETER = "-metric";
    private final String METRIC_OPTION = "AE";
    private final String SUBIMAGE_SEARCH_OPTION = "-subimage-search";
    private final String DISSIMILAR_THRESHOLD = "-dissimilarity-threshold";
    private final String DISSIMILAR_THRESHOLD_VALUE = "100%";

    private File[] getActualScreenshotFiles() {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(ACTUAL_SCREENSHOT_FILE_PREFIX);
            }
        };
        return new File(ACTUAL_SCREENS_PATH).listFiles(fileFilter);
    }

    private File[] getExpectedScreenshotFiles() {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(EXPECTED_SCREENSHOT_FILE_PREFIX);
            }
        };
        return new File(EXPECTED_SCREENS_PATH).listFiles(fileFilter);
    }

    public void compareAndCaptureResults() {
        File[] actualFiles = getActualScreenshotFiles();
        Arrays.sort(actualFiles, NameFileComparator.NAME_COMPARATOR);
        File[] expectedFiles = getExpectedScreenshotFiles();
        Arrays.sort(expectedFiles, NameFileComparator.NAME_COMPARATOR);
        try {
            csvReportBuilder.setColumnHeaders(getCSVReportHeaders());
            compareAndStoreResults(actualFiles, expectedFiles);
            csvReportBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCSVReportHeaders() {
        List<String> csvHeaders = new ArrayList<String>();
        csvHeaders.add("Expected Filename");
        csvHeaders.add("Actual Filename");
        csvHeaders.add("Total Image Pixels [Expected] (width * height)");
        csvHeaders.add("Total Image Pixels [Actual] (width * height)");
        csvHeaders.add("Diff Outcome (Pixel Difference)");
        csvHeaders.add("Pixel Deviation (%)");
        csvHeaders.add("Comparison Strategy Used");
        csvHeaders.add("Notes");
        csvHeaders.add("Command Executed");
        return csvHeaders;
    }

    private void compareAndStoreResults(File[] actualFiles, File[] expectedFiles) throws IOException, InterruptedException {
        for(int i = 0; i < expectedFiles.length; i++) {
            String diffScreensPath = DIFF_SCREENS_PATH + "diff_" + actualFiles[i].getName().replaceFirst(ACTUAL_SCREENSHOT_FILE_PREFIX, "");
            ResultRow resultRow = new ResultRow();
            String[] command = buildCommandArray(actualFiles[i], expectedFiles[i], diffScreensPath, resultRow);
            Process process = Runtime.getRuntime().exec(command);
            StreamGobbler errorGobbler = gobbleStream(process);
            System.out.println("Exit Value: " + process.waitFor());
            captureAndStoreResults(resultRow, errorGobbler.getOutputLine(), command);
        }
    }

    private String[] buildCommandArray(File actualFile, File expectedFile, String diffScreensPath, ResultRow resultRow) throws IOException {
        calculateAndStoreImagePixelValues(actualFile, expectedFile, resultRow);
        List<String> commands = new ArrayList<String>();
        commands.add(PATH_TO_IM_BINARY);
        commands.add(METRIC_PARAMETER);
        commands.add(METRIC_OPTION);
        buildCommandAccordingToImageSizeDifference(resultRow, commands);
        commands.add(diffScreensPath);
        return commands.toArray(new String[commands.size()]);
    }

    private void calculateAndStoreImagePixelValues(File actualFile, File expectedFile, ResultRow resultRow) throws IOException {
        BufferedImage expectedBufferedImage = ImageIO.read(expectedFile);
        BufferedImage actualBufferedImage = ImageIO.read(actualFile);
        BigDecimal expectedTotalPixels = BigDecimal.valueOf(expectedBufferedImage.getWidth() * expectedBufferedImage.getHeight());
        BigDecimal actualTotalPixels = BigDecimal.valueOf(actualBufferedImage.getWidth() * actualBufferedImage.getHeight());
        resultRow.setExpectedTotalPixels(expectedTotalPixels);
        resultRow.setActualTotalPixels(actualTotalPixels);
        resultRow.setExpectedFileName(expectedFile.getName());
        resultRow.setActualFileName(actualFile.getName());
    }

    private void buildCommandAccordingToImageSizeDifference(ResultRow resultRow, List<String> commands) {
        if(!resultRow.getExpectedTotalPixels().equals(resultRow.getActualTotalPixels())) {
            commands.add(SUBIMAGE_SEARCH_OPTION);
            commands.add(DISSIMILAR_THRESHOLD);
            commands.add(DISSIMILAR_THRESHOLD_VALUE);
        }
        if(resultRow.getExpectedTotalPixels().doubleValue() > resultRow.getActualTotalPixels().doubleValue()) {
            commands.add(EXPECTED_SCREENS_PATH + resultRow.getExpectedFileName());
            commands.add(ACTUAL_SCREENS_PATH + resultRow.getActualFileName());
        } else {
            commands.add(ACTUAL_SCREENS_PATH + resultRow.getActualFileName());
            commands.add(EXPECTED_SCREENS_PATH + resultRow.getExpectedFileName());
        }
    }

    private void captureAndStoreResults(ResultRow resultRow, String output, String[] command) throws IOException {
        setComparisonStrategyUsed(resultRow, output);
        if(resultRow.getStrategyUsed().equals(ComparisonStrategy.SUB_IMAGE)) {
            output = output.replace(output.substring(output.indexOf("@"), output.length()), "").trim();
        }
        BigDecimal percentageDeviation = calculatePercentageDeviation(resultRow.getExpectedTotalPixels(), output);
        resultRow.setPercentageDeviation(percentageDeviation);
        resultRow.setOutput(output);
        resultRow.setCommandExecuted(StringUtils.join(command, " "));
        csvReportBuilder.addColumnValues(resultRow.getResultsAsList());
    }

    private void setComparisonStrategyUsed(ResultRow resultRow, String output) {
        if(output.startsWith("compare")) {
            resultRow.setStrategyUsed(ComparisonStrategy.ERROR);
            resultRow.setNotes("Error in comparison execution");
        } else if(output.contains("@")) {
            resultRow.setStrategyUsed(ComparisonStrategy.SUB_IMAGE);
        } else {
            resultRow.setStrategyUsed(ComparisonStrategy.ONE_TO_ONE);
        }
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
