package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import vision.gui.TitledComponent;
import vision.objectRecognition.detection.ImageManipulatorWithOptions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Properties;

import static vision.objectRecognition.detection.manipulators.BackgroundSubtractionMOG.BACKGROUND_PATH;
import static vision.objectRecognition.detection.manipulators.BackgroundSubtractionMOG.BACKGROUND_PATH_PREF;
import static vision.objectRecognition.utils.Subtractor.subtract;

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

    private JSlider allThresholds = new JSlider(0,100,0);
    private JSlider redSlider     = new JSlider(0,100,0);
    private JSlider greenSlider   = new JSlider(0,100,0);
    private JSlider blueSlider    = new JSlider(0,100,0);

    private JPanel sliders = new JPanel(new GridLayout(4,1));

    @Override
    public Component getModificationGUI() {
        return sliders;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(ALL_THRESH_PROP, String.valueOf(allThresholds.getValue()));
        prop.setProperty(R_THRESH_PROP, String.valueOf(redSlider.getValue()));
        prop.setProperty(G_THRESH_PROP, String.valueOf(greenSlider.getValue()));
        prop.setProperty(B_THRESH_PROP, String.valueOf(blueSlider.getValue()));
    }

    @Override
    public void loadModificationSettings(Properties prop) {
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
//        BACKGROUND_PATH = prop.getProperty(BACKGROUND_PATH_PREF, BACKGROUND_PATH);
    }

    public BackgroundSubtractionThreshold() {
        allThresholds.addChangeListener(this);
        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);

        sliders.add(new TitledComponent("All Thresholds", allThresholds));
        sliders.add(new TitledComponent("Blue/Hue Threshold: ", redSlider));
        sliders.add(new TitledComponent("Green/Saturation Threshold: ", greenSlider));
        sliders.add(new TitledComponent("Red/Value Threshold: ", blueSlider));
    }


    @Override
    protected Mat run(Mat input) {
        Mat backgroundImage =
                Imgcodecs.imread(BACKGROUND_PATH);

        if (backgroundImage.height() != 0 || backgroundImage.width() != 0) {
            Mat subtractedImage = subtract(backgroundImage, input);

            Mat thresholdedImage = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
            Core.inRange(subtractedImage,
                    new Scalar(THRESHOLD_B, THRESHOLD_G, THRESHOLD_R),
                    new Scalar(255, 255, 255),
                    thresholdedImage);

            return thresholdedImage;
        }
        return input;
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
