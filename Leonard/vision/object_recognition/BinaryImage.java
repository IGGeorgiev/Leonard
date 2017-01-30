package vision.object_recognition;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import vision.capture.FrameReceivedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToBinaryImage;
import static vision.utils.Converter.matToImage;
import static vision.utils.Normalizer.normalizeRGB;
import static vision.utils.Subtractor.subtract;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Label container class for BinaryImage
 */
public class BinaryImage extends JLabel implements FrameReceivedListener {

    public BinaryImage() {
        super();
    }

    @Override
    public void onFrameReceived(BufferedImage image) {
        BufferedImage norm = normalizeRGB(image);
        BufferedImage background = null;

        try {
             background =
                    ImageIO.read(new File("Leonard/vision/calibration/pre_saved_values/empty_pitch_norm.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mat bg = imageToMat(background);
        Mat curr = imageToMat(norm);
        Mat beforeThreshold = subtract(curr, bg);
        Mat thresholdedImage = new Mat();

        Core.inRange(beforeThreshold,
                new Scalar(4, 4, 4),
                new Scalar(255,255,255),
                thresholdedImage);

//      DEBUG

//        this.getGraphics().drawImage(norm,
//                5, 0,
//                thresholdedImage.width() / 2,
//                thresholdedImage.height() / 2,
//                null);
//
//        for (int x = 0; x < thresholdedImage.width(); x++) {
//            for (int y = 0; y < thresholdedImage.height(); y++) {
//                double[] out = beforeThreshold.get(x,y);
//                System.out.println(out[0]);
//            }
//        }

        BufferedImage binaryImage = matToBinaryImage(thresholdedImage);

        this.getGraphics().drawImage(binaryImage,
                0, 0,
                thresholdedImage.width(),
                thresholdedImage.height(),
                null);
    }
}
