package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.objectRecognition.detection.ImageManipulator;

/**
 * BGR to HSV converter
 * Created by Ivan Georgiev (s1410984) on 16/02/17.
 */
public class HSVConverter extends ImageManipulator {
    @Override
    protected Mat run(Mat input) {
        Mat out = new Mat();
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
        return out;
    }
}
