package vision.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import vision.capture.FrameReceivedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToBinaryImage;
import static vision.utils.Normalizer.normalizeRGB;
import static vision.utils.Subtractor.subtract;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Label container class for BinaryImage
 */
public class BinaryImage extends JLabel implements FrameReceivedListener {

    private static int THRESHOLD_R = 0, THRESHOLD_G = 0, THRESHOLD_B = 0;
    private static JSlider r, g, b;

    private static int GAUSSIAN_BLUR_SIZE = 3;

    private List<FrameReceivedListener> frameReceivedListeners = new ArrayList<>();

    public BinaryImage(JSlider r, JSlider g, JSlider b) {
        super();
        BinaryImage.r = r;
        BinaryImage.g = g;
        BinaryImage.b = b;
    }

    public void addFrameReceivedListener(FrameReceivedListener l) {
        frameReceivedListeners.add(l);
    }

    @Override
    public void onFrameReceived(BufferedImage image) {
        BufferedImage norm = normalizeRGB(image);
        BufferedImage background;

        try {
             background =
                    ImageIO.read(new File("Leonard/vision/calibration/pre_saved_values/empty_pitch_norm.jpg"));
        } catch (IOException e) {
//            e.printStackTrace();
            // Ignore so as to not crash application if file is updated
            return;
        }

        Mat bg = imageToMat(background);
        Mat curr = imageToMat(norm);
        Mat beforeThreshold = subtract(curr, bg);
        Mat thresholdedImage = new Mat();

        Core.inRange(beforeThreshold,
                new Scalar(THRESHOLD_R, THRESHOLD_G, THRESHOLD_B),
                new Scalar(255,255,255),
                thresholdedImage);


        // Apply gaussian blur
        Mat blurredMat = new Mat();
        Imgproc.GaussianBlur(thresholdedImage, blurredMat, new Size(GAUSSIAN_BLUR_SIZE, GAUSSIAN_BLUR_SIZE), 0);


        BufferedImage binaryImage = matToBinaryImage(blurredMat);


        // Broadcast image to other transformation functions
        for (FrameReceivedListener l : frameReceivedListeners) {
            new Thread(() -> l.onFrameReceived(binaryImage)).run();
        }


        this.getGraphics().drawImage(binaryImage,
                0, 0,
                thresholdedImage.width(),
                thresholdedImage.height(),
                null);
    }


    public static class ThresholdChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            THRESHOLD_R = value;
            r.setValue(value);
            THRESHOLD_G = value;
            g.setValue(value);
            THRESHOLD_B = value;
            b.setValue(value);
        }
    }


    public static class RThresholdChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            THRESHOLD_R = slider.getValue();
        }
    }

    public static class GThresholdChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            THRESHOLD_G = slider.getValue();
        }
    }

    public static class BThresholdChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            THRESHOLD_B = slider.getValue();
        }
    }

    public static class GaussianBlurChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            if (value % 2 == 1)
                GAUSSIAN_BLUR_SIZE = slider.getValue();
        }
    }

}
