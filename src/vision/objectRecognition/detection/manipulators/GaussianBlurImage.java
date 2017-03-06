package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import vision.objectRecognition.detection.ImageManipulatorWithOptions;
import vision.gui.TitledComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 02/02/17.
 * Class to Apply Gaussian Blur to Image
 */
public class GaussianBlurImage extends ImageManipulatorWithOptions implements ChangeListener {

    private static int filterCount = 0;

    private final String GAUSSIAN_BLUR_PROP = "GaussianBlur" + filterCount;

    private static int GAUSSIAN_BLUR_SIZE = 3;

    private JSlider gaussianBlur = new JSlider(0, 21, 1);
    private TitledComponent gui = new TitledComponent("Gaussian Blur " + filterCount + " :", gaussianBlur);

    public GaussianBlurImage() {
        filterCount++;
        gaussianBlur.addChangeListener(this);
    }

    @Override
    protected Mat run(Mat inputMat) {
        if (GAUSSIAN_BLUR_SIZE != 0) {

            // Apply gaussian blur
            Mat blurredMat = new Mat();
            Imgproc.GaussianBlur(inputMat, blurredMat,
                    new Size(GAUSSIAN_BLUR_SIZE, GAUSSIAN_BLUR_SIZE), 0);

            return blurredMat;
        } else {
            return inputMat;
        }
    }

    @Override
    public Component getModificationGUI() {
        return gui;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(GAUSSIAN_BLUR_PROP, String.valueOf(GAUSSIAN_BLUR_SIZE));
    }

    @Override
    public void loadModificationSettings(Properties prop) {
        GAUSSIAN_BLUR_SIZE =
                Integer.valueOf(
                        prop.getProperty(GAUSSIAN_BLUR_PROP, String.valueOf(GAUSSIAN_BLUR_SIZE))
                );
        gaussianBlur.setValue(GAUSSIAN_BLUR_SIZE);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int value = slider.getValue();
        if (value % 2 == 1 || value == 0)
            GAUSSIAN_BLUR_SIZE = value;
    }

    @Override
    public void onFrameReceived(Mat image, long time) {
        manipulatedImage = run(image);
        if (manipulatedImage != null) {
            if (isDisplayed) {
                BufferedImage frame = catchFrame();
                if (frame != null && manipulatorDisplay.getGraphics() != null)
                    if (manipulatorDisplay != null)
                        manipulatorDisplay.getGraphics().drawImage(catchFrame(), 0, 0, null);
            }
            if (nextManipulator != null) {
                Mat out = new Mat();
                manipulatedImage.copyTo(out);
                new Thread(() -> nextManipulator.onFrameReceived(out, time)).run();
            }
        }
    }
}
