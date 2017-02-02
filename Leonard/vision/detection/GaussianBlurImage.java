package vision.detection;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import vision.ImageManipulatorWithOptions;
import vision.TitledComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

import static vision.utils.Converter.binaryImageToMat;
import static vision.utils.Converter.matToBinaryImage;

/**
 * Created by Ivan Georgiev (s1410984) on 02/02/17.
 * Class to Apply Gaussian Blur to Image
 */
public class GaussianBlurImage extends ImageManipulatorWithOptions implements ChangeListener {

    private static final String GAUSSIAN_BLUR_PROP = "GaussianBlur";

    private static int GAUSSIAN_BLUR_SIZE = 3;

    private JSlider gaussianBlur = new JSlider(0, 11, 1);
    private TitledComponent gui = new TitledComponent("Gaussian Blur: ", gaussianBlur);

    public GaussianBlurImage() {
        gaussianBlur.addChangeListener(this);
    }

    @Override
    protected BufferedImage run(BufferedImage input) {
        if (GAUSSIAN_BLUR_SIZE != 0) {
            Mat inputMat = binaryImageToMat(input);

            // Apply gaussian blur
            Mat blurredMat = new Mat();
            Imgproc.GaussianBlur(inputMat, blurredMat,
                    new Size(GAUSSIAN_BLUR_SIZE, GAUSSIAN_BLUR_SIZE), 0);

            return matToBinaryImage(blurredMat);
        } else {
            return input;
        }
    }

    @Override
    protected Component getModificationGUI() {
        return gui;
    }

    @Override
    protected void saveModificationSettings(Properties prop) {
        prop.setProperty(GAUSSIAN_BLUR_PROP, String.valueOf(GAUSSIAN_BLUR_SIZE));
    }

    @Override
    protected void loadModificationSettings(Properties prop) {
        GAUSSIAN_BLUR_SIZE =
                Integer.valueOf(
                        prop.getProperty(GAUSSIAN_BLUR_PROP, String.valueOf(GAUSSIAN_BLUR_SIZE))
                );
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int value = slider.getValue();
        if (value % 2 == 1 || value == 0)
            GAUSSIAN_BLUR_SIZE = value;
    }
}
