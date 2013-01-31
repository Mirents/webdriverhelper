package util;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ImageTest {

    @Test
    public void testGetImageInformation() {
        BigDecimal totalPixels = new BigDecimal(400);
        BigDecimal widthInPixels = new BigDecimal(20);
        BigDecimal heightInPixels = new BigDecimal(20);
        ClassLoader classLoader = this.getClass().getClassLoader();
        String pathToImageFile = classLoader.getResource("imagetest/test.jpg").getFile();
        Image image = new Image(pathToImageFile);
        Assert.assertEquals(totalPixels, image.getTotalPixels());
        Assert.assertEquals(widthInPixels, image.getWidthInPixels());
        Assert.assertEquals(heightInPixels, image.getHeightInPixels());
        Assert.assertEquals("test.jpg", image.getImageFile().getName());
    }
}
