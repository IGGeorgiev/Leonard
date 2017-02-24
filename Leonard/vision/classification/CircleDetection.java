package vision.classification;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.detection.ImageManipulator;

/**
 * Created by Ivan Georgiev (s1410984) on 24/02/17.
 */
public class CircleDetection extends ImageManipulator {

    @Override
    protected Mat run(Mat input) {

        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1, 10);

        return circles;
    }
}
