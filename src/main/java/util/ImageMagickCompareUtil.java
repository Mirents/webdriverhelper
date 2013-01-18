package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.*;
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
    private final String ACTUAL_SCREENS_PATH = "screenshot/actual/";
    private final String EXPECTED_SCREENS_PATH = "screenshot/expected/";
    private final String DIFF_SCREENS_PATH = "screenshot/diff/";
    private final String PATH_TO_IM_BINARY = "/opt/local/bin/compare";

    private File[] getActualScreenshotFiles() {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("actual_");
            }
        };
        return new File(ACTUAL_SCREENS_PATH).listFiles(fileFilter);
    }

    private File[] getExpectedScreenshotFiles() {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("expected_");
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
            String fileHeader = "Expected Filename, Actual Filename, Outcome (Pixel Difference)";
            results.add(fileHeader);
            compareAndWriteResultsToFile(actualFiles, expectedFiles, results);
            FileUtils.writeLines(new File("screenshot/results.csv"), results);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compareAndWriteResultsToFile(File[] actualFiles, File[] expectedFiles, List<String> results) throws IOException, InterruptedException {
        for(int i = 0; i < expectedFiles.length; i++) {
            String[] command = {PATH_TO_IM_BINARY,
                            "-metric",
                            "AE",
                            EXPECTED_SCREENS_PATH + expectedFiles[i].getName(),
                            ACTUAL_SCREENS_PATH + actualFiles[i].getName(),
                            DIFF_SCREENS_PATH + "diff_" + actualFiles[i].getName()};
            Process process = Runtime.getRuntime().exec(command);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error Stream");
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Input Stream");
            errorGobbler.start();
            outputGobbler.start();
            int exitValue = process.waitFor();
            System.out.println("Exit Value: " + exitValue);
            String resultLine = expectedFiles[i].getName() +
                                "," + actualFiles[i].getName() +
                                "," + errorGobbler.getOutputLine();
            results.add(resultLine);
        }
    }
}
