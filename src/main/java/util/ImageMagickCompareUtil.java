package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class StreamGobbler extends Thread {
    private InputStream inputStream;
    private String type;
    private String outputLine = "";

    public StreamGobbler(InputStream inputStream, String type) {
        this.inputStream = inputStream;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                outputLine += line;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public String getOutputLine() {
        return outputLine;
    }

}

public class ImageMagickCompareUtil {
    private final String ACTUAL_SCREENSHOT_FILE_PREFIX = "actual_";
    private final String EXPECTED_SCREENSHOT_FILE_PREFIX = "expected_";
    private final String ACTUAL_SCREENS_PATH = "screenshot/actual/";
    private final String EXPECTED_SCREENS_PATH = "screenshot/expected/";
    private final String DIFF_SCREENS_PATH = "screenshot/diff/";
    private final String PATH_TO_IM_BINARY = "/opt/local/bin/compare";
    private final String RESULTS_FILE_PATH = "screenshot/results.csv";

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
            List<String> results = new ArrayList<String>();
            String fileHeader = "Expected Filename, " +
                                "Actual Filename, " +
                                "Total Image Pixels [Expected] (width * height), " +
                                "Total Image Pixels [Actual] (width * height), " +
                                "Diff Outcome (Pixel Difference), " +
                                "Pixel deviation (%)";
            results.add(fileHeader);
            compareAndWriteResultsToFile(actualFiles, expectedFiles, results);
            FileUtils.writeLines(new File(RESULTS_FILE_PATH), results);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compareAndWriteResultsToFile(File[] actualFiles, File[] expectedFiles, List<String> results) throws IOException, InterruptedException {
        for(int i = 0; i < expectedFiles.length; i++) {
            String diffScreensPath = DIFF_SCREENS_PATH + "diff_" + actualFiles[i].getName().replaceFirst(ACTUAL_SCREENSHOT_FILE_PREFIX, "");
            String[] command = {PATH_TO_IM_BINARY,
                            "-metric",
                            "AE",
                            EXPECTED_SCREENS_PATH + expectedFiles[i].getName(),
                            ACTUAL_SCREENS_PATH + actualFiles[i].getName(),
                            diffScreensPath};
            Process process = Runtime.getRuntime().exec(command);
            StreamGobbler errorGobbler = gobbleStream(process);
            int exitValue = process.waitFor();
            System.out.println("Exit Value: " + exitValue);
            String result = errorGobbler.getOutputLine();
            captureAndStoreResults(actualFiles[i], expectedFiles[i], results, result);
        }
    }

    private void captureAndStoreResults(File actualFile, File expectedFile, List<String> results, String result) throws IOException {
        BufferedImage expectedBufferedImage = ImageIO.read(expectedFile);
        BufferedImage actualBufferedImage = ImageIO.read(actualFile);
        BigDecimal expectedTotalPixels = BigDecimal.valueOf(expectedBufferedImage.getWidth() * expectedBufferedImage.getHeight());
        BigDecimal actualTotalPixels = BigDecimal.valueOf(actualBufferedImage.getWidth() * actualBufferedImage.getHeight());
        BigDecimal percentageDeviation = calculatePercentageDeviation(expectedTotalPixels, result);
        String resultLine = expectedFile.getName() +
                            "," + actualFile.getName() +
                            "," + expectedTotalPixels +
                            "," + actualTotalPixels +
                            "," + result +
                            "," + percentageDeviation;
        results.add(resultLine);
    }

    private BigDecimal calculatePercentageDeviation(BigDecimal totalImagePixels, String result) throws IOException {
        BigDecimal percentageDeviation;
        try {
            BigDecimal pixelDifference = new BigDecimal(result);
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
