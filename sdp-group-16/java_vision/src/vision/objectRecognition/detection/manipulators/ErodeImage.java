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
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 02/02/17.
 * An image erosion manipulation
 */
public class ErodeImage extends ImageManipulatorWithOptions implements ChangeListener{

    private static final String EROSION_PROP = "Erosion";

    private JSlider erosionSlider = new JSlider(1, 20, 1);
    private Component gui = new TitledComponent("Erosion: ", erosionSlider);

    private static int EROSION_SIZE = 1;

    public ErodeImage() {
        erosionSlider.addChangeListener(this);
    }


    @Override
    public Component getModificationGUI() {
        return gui;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(EROSION_PROP, String.valueOf(EROSION_SIZE));
    }

    @Override
    public void loadModificationSettings(Properties prop) {
        EROSION_SIZE = Integer.valueOf(prop.getProperty(EROSION_PROP, String.valueOf(EROSION_SIZE)));
        erosionSlider.setValue(EROSION_SIZE);
    }

    @Override
    protected Mat run(Mat image) {
        Mat erodedImage = new Mat();
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE,
                new Size(EROSION_SIZE, EROSION_SIZE));
        Imgproc.erode(image, erodedImage, erosionElement);
        return erodedImage;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        EROSION_SIZE = slider.getValue();
    }
}
