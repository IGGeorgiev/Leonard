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
    protected Component getModificationGUI() {
        return gui;
    }

    @Override
    protected void saveModificationSettings(Properties prop) {
        prop.setProperty(EROSION_PROP, String.valueOf(EROSION_SIZE));
    }

    @Override
    protected void loadModificationSettings(Properties prop) {
        EROSION_SIZE = Integer.valueOf(prop.getProperty(EROSION_PROP, String.valueOf(EROSION_SIZE)));
    }

    @Override
    protected BufferedImage run(BufferedImage input) {
        Mat image = binaryImageToMat(input);
        Mat erodedImage = new Mat();
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE,
                new Size(EROSION_SIZE, EROSION_SIZE));
        Imgproc.erode(image, erodedImage, erosionElement);
        return matToBinaryImage(erodedImage);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        EROSION_SIZE = slider.getValue();
    }
}
