package vision.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import vision.detection.ImageManipulator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 23/02/17.
 */
public class NonLocalMeansDenoising extends ImageManipulator {

    private LinkedList<Mat> images = new LinkedList<>();

    @Override
    protected Mat run(Mat input) {
        images.addFirst(input);
        if (images.size() == 4) {// vary
            images.removeLast();
            Mat out = new Mat();
            Photo.fastNlMeansDenoisingColoredMulti(images, out, 1, 1);
            return out;
        }
        return input;
    }
}
