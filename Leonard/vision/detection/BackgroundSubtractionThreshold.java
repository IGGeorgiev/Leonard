package vision.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import vision.ImageManipulatorWithOptions;
import vision.TitledComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToBinaryImage;
import static vision.utils.Subtractor.subtract;

/**
 * Created by Ivan Georgiev (s1410984) on 02/02/17.
 * Class which subtracts the background from the current frame and
 * thresholds the resulting image to produce a binary image
 */
public class BackgroundSubtractionThreshold extends ImageManipulatorWithOptions implements ChangeListener {

    private static final String ALL_THRESH_PROP = "AllRGBThresholds";
    private static final String R_THRESH_PROP = "RedThreshold";
    private static final String G_THRESH_PROP = "GreenThreshold";
    private static final String B_THRESH_PROP = "BlueThreshold";

    private int THRESHOLD_R = 0, THRESHOLD_G = 0, THRESHOLD_B = 0;

    private JSlider allThresholds = new JSlider(0,255,0);
    private JSlider redSlider     = new JSlider(0,255,0);
    private JSlider greenSlider   = new JSlider(0,255,0);
    private JSlider blueSlider    = new JSlider(0,255,0);

    private JPanel sliders = new JPanel(new GridLayout(4,1));

    @Override
    protected Component getModificationGUI() {
        return sliders;
    }

    @Override
    protected void saveModificationSettings(Properties prop) {
        prop.setProperty(ALL_THRESH_PROP, String.valueOf(allThresholds.getValue()));
        prop.setProperty(R_THRESH_PROP, String.valueOf(redSlider.getValue()));
        prop.setProperty(G_THRESH_PROP, String.valueOf(greenSlider.getValue()));
        prop.setProperty(B_THRESH_PROP, String.valueOf(blueSlider.getValue()));
    }

    @Override
    protected void loadModificationSettings(Properties prop) {
        // Assume if last saved is null all are null
        allThresholds.setValue(Integer.valueOf(
                prop.getProperty(ALL_THRESH_PROP, String.valueOf(allThresholds.getValue()))
        ));
        redSlider.setValue(Integer.valueOf(
                prop.getProperty(R_THRESH_PROP, String.valueOf(redSlider.getValue()))
        ));
        greenSlider.setValue(Integer.valueOf(
                prop.getProperty(G_THRESH_PROP, String.valueOf(greenSlider.getValue()))
        ));
        blueSlider.setValue(Integer.valueOf(
                prop.getProperty(B_THRESH_PROP, String.valueOf(blueSlider.getValue()))
        ));
    }

    public BackgroundSubtractionThreshold() {
        allThresholds.addChangeListener(this);
        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);

        sliders.add(new TitledComponent("All Thresholds", allThresholds));
        sliders.add(new TitledComponent("Red Threshold: ", redSlider));
        sliders.add(new TitledComponent("Green Threshold: ", greenSlider));
        sliders.add(new TitledComponent("Blue Threshold: ", blueSlider));
    }


    @Override
    protected BufferedImage run(BufferedImage input) {
        Mat backgroundImage =
                Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/empty_pitch_norm.png");

        Mat subtractedImage = subtract(backgroundImage, imageToMat(input));

        Mat thresholdedImage = new Mat();
        Core.inRange(subtractedImage,
                new Scalar(THRESHOLD_R, THRESHOLD_B, THRESHOLD_G),
                new Scalar(255,255,255),
                thresholdedImage);

        return matToBinaryImage(thresholdedImage);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        if (slider.equals(allThresholds)) {
            // Set values
            THRESHOLD_R = slider.getValue();
            THRESHOLD_B = slider.getValue();
            THRESHOLD_G = slider.getValue();

            // Update other sliders
            redSlider.setValue(THRESHOLD_R);
            blueSlider.setValue(THRESHOLD_B);
            greenSlider.setValue(THRESHOLD_G);

        } else if (slider.equals(redSlider)) {
            THRESHOLD_R = slider.getValue();
        } else if (slider.equals(greenSlider)) {
            THRESHOLD_G = slider.getValue();
        } else if (slider.equals((blueSlider))) {
            THRESHOLD_B = slider.getValue();
        }
    }
}
