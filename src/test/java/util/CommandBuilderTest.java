package util;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class CommandBuilderTest {

    @Test
    public void testCommandBuiltWithOneToOneComparison() {
        CommandBuilder commandBuilder = new CommandBuilder();
        String pathToExpectedFile = "expected/test.png";
        String pathToActualFile = "actual/test.png";
        String pathToDiffFile = "diff/test.png";
        commandBuilder.setSecondImagePixels(new BigDecimal(1000));
        commandBuilder.setFirstImagePixels(new BigDecimal(1000));
        commandBuilder.setPathToImageMagickBinary("/opt/local/bin/compare");
        commandBuilder.setFilePaths(pathToExpectedFile, pathToActualFile, pathToDiffFile);
        commandBuilder.setOneToOneComparisonMetricParameters();
        commandBuilder.build();
        String[] commands = commandBuilder.getCommandAsArray();
        String command = commandBuilder.toString();
        Assert.assertEquals(6, commands.length);
        String expectedCommand = "/opt/local/bin/compare " +
                                 "-metric AE " +
                                 "expected/test.png " +
                                 "actual/test.png " +
                                 "diff/test.png";
        Assert.assertEquals(expectedCommand, command);
    }

    @Test
    public void testCommandBuiltWithSubImageComparison() {
        CommandBuilder commandBuilder = new CommandBuilder();
        String pathToExpectedFile = "expected/test.png";
        String pathToActualFile = "actual/test.png";
        String pathToDiffFile = "diff/test.png";
        commandBuilder.setSecondImagePixels(new BigDecimal(1000));
        commandBuilder.setFirstImagePixels(new BigDecimal(400));
        commandBuilder.setPathToImageMagickBinary("/opt/local/bin/compare");
        commandBuilder.setFilePaths(pathToExpectedFile, pathToActualFile, pathToDiffFile);
        commandBuilder.setSubImageMetricParameters();
        commandBuilder.build();
        String[] commands = commandBuilder.getCommandAsArray();
        String actualCommand = commandBuilder.toString();
        Assert.assertEquals(9, commands.length);
        String expectedCommand = "/opt/local/bin/compare " +
                                 "-metric AE " +
                                 "-subimage-search " +
                                 "-dissimilarity-threshold 100% " +
                                 "actual/test.png " +
                                 "expected/test.png " +
                                 "diff/test.png";
        Assert.assertEquals(expectedCommand, actualCommand);
    }
}
