package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import vision.objectRecognition.detection.ImageManipulatorWithOptions;
import vision.gui.TitledComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 06/02/17.
 * Manipulation to remove small distortion blobs from binary image
 */
public class RemoveSmallBlobs extends ImageManipulatorWithOptions implements ChangeListener{

    private static final String THRESHOLD_SIZE_PROPERTY = "BlobSizeThreshold";

    private double THRESHOLD_SIZE = 100;
    private JSlider blobSizeSlider = new JSlider(0, 300, 100);
    private Component gui = new TitledComponent("Small Blob Size: ", blobSizeSlider);

    public RemoveSmallBlobs() {
        blobSizeSlider.addChangeListener(this);
    }

    @Override
    protected Mat run(Mat input) {
        List<MatOfPoint> contours = new ArrayList<>();
        List<Integer> small_blobs = new ArrayList<>();

        Imgproc.findContours(input, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE );

        for (int i = 0; i < contours.size(); i++) {
            double contour_area = Imgproc.contourArea(contours.get(i));
            if ( contour_area < THRESHOLD_SIZE)
                small_blobs.add(i);
        }

        for (int contour : small_blobs) {
            Imgproc.drawContours(input, contours, contour, new Scalar(0), Imgproc.CV_WARP_FILL_OUTLIERS);
        }

        return input;
    }

    @Override
    public Component getModificationGUI() {
        return gui;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(THRESHOLD_SIZE_PROPERTY, String.valueOf(THRESHOLD_SIZE));
    }

    @Override
    public void loadModificationSettings(Properties prop) {
        THRESHOLD_SIZE = Double.valueOf(
                prop.getProperty(THRESHOLD_SIZE_PROPERTY, String.valueOf(THRESHOLD_SIZE))
        );
        blobSizeSlider.setValue(((Double) THRESHOLD_SIZE).intValue());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        THRESHOLD_SIZE = slider.getValue();
    }
}
