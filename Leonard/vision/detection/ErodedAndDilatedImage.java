package vision.detection;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import vision.capture.FrameReceivedListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToBinaryImage;

/**
 * Created by Ivan Georgiev (s1410984) on 30/01/17.
 * Class to Erode And Dilate binary image
 */
public class ErodedAndDilatedImage extends JLabel implements FrameReceivedListener {

    private static double EROSION_SIZE = 1;
    private static double DILATION_SIZE = 1;

    public ErodedAndDilatedImage() {
        super();
    }

    @Override
    public void onFrameReceived(BufferedImage image) {
        Mat binaryImage = imageToMat(image);

        Mat dilatedImage = new Mat();
        Mat dilationElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(DILATION_SIZE, DILATION_SIZE));
        Imgproc.dilate(binaryImage, dilatedImage, dilationElement);


        Mat erodedImage = new Mat();
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE,
                new Size(EROSION_SIZE, EROSION_SIZE));
        Imgproc.erode(dilatedImage, erodedImage, erosionElement);


        BufferedImage erodedAndDilated = matToBinaryImage(erodedImage);

        if (erodedAndDilated != null) {
            // Draw
            this.getGraphics()
                    .drawImage(erodedAndDilated, 0, 0,
                            erodedAndDilated.getWidth(), erodedAndDilated.getHeight(), null);
        }
    }

    public static class ErosionChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            EROSION_SIZE = slider.getValue();
        }
    }

    public static class DialationChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            DILATION_SIZE = slider.getValue();
        }
    }
}
