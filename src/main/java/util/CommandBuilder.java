package util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    private String pathToImageMagickBinary;
    private final String METRIC_PARAMETER = "-metric";
    private final String METRIC_OPTION = "AE";
    private final String SUBIMAGE_SEARCH_OPTION = "-subimage-search";
    private final String DISSIMILAR_THRESHOLD = "-dissimilarity-threshold";
    private final String DISSIMILAR_THRESHOLD_VALUE = "100%";
    private String pathToFirstImage;
    private String pathToSecondImage;
    private String pathToDiffFile;
    private List<String> metrics;
    private List<String> commands;
    private BigDecimal firstImagePixels;
    private BigDecimal secondImagePixels;

    public CommandBuilder() {
        this.commands = new ArrayList<String>();
        this.metrics = new ArrayList<String>();
    }

    public void setPathToImageMagickBinary(String pathToImageMagickBinary) {
        this.pathToImageMagickBinary = pathToImageMagickBinary;
    }

    public void setMetricParameters(String... parameters) {
        for(String parameter : parameters) {
            this.metrics.add(parameter);
        }
    }

    private void setImageMetricParameters() {
        if(firstImagePixels.longValue() != secondImagePixels.longValue()) {
            setSubImageMetricParameters();
        } else {
            setOneToOneComparisonMetricParameters();
        }
    }

    public void setOneToOneComparisonMetricParameters() {
        this.metrics.clear();
        this.metrics.add(METRIC_PARAMETER);
        this.metrics.add(METRIC_OPTION);
    }

    public void setSubImageMetricParameters() {
        setOneToOneComparisonMetricParameters();
        this.metrics.add(SUBIMAGE_SEARCH_OPTION);
        this.metrics.add(DISSIMILAR_THRESHOLD);
        this.metrics.add(DISSIMILAR_THRESHOLD_VALUE);
    }

    public void setFilePaths(String pathToFirstImage, String pathToSecondImage, String pathToDiffFile) {
        this.pathToFirstImage = pathToFirstImage;
        this.pathToSecondImage = pathToSecondImage;
        this.pathToDiffFile = pathToDiffFile;
    }

    public BigDecimal getFirstImagePixels() {
        return firstImagePixels;
    }

    public void setFirstImagePixels(BigDecimal firstImagePixels) {
        this.firstImagePixels = firstImagePixels;
    }

    public BigDecimal getSecondImagePixels() {
        return secondImagePixels;
    }

    public void setSecondImagePixels(BigDecimal secondImagePixels) {
        this.secondImagePixels = secondImagePixels;
    }

    public void build() {
        this.commands.add(pathToImageMagickBinary);
        setImageMetricParameters();
        this.commands.addAll(metrics);
        if(secondImagePixels.longValue() > firstImagePixels.longValue()) {
            this.commands.add(pathToSecondImage);
            this.commands.add(pathToFirstImage);
        } else {
            this.commands.add(pathToFirstImage);
            this.commands.add(pathToSecondImage);
        }
        this.commands.add(pathToDiffFile);
    }

    public String toString() {
        return StringUtils.join(getCommandAsArray(), " ");
    }

    public String[] getCommandAsArray() {
        String[] commandsArray = new String[commands.size()];
        return this.commands.toArray(commandsArray);
    }
}
