package vision.classification;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import vision.ImageManipulationPipeline;
import vision.detection.ImageManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 19/02/17.
 * An implementation of the SIFT/SURF (Sped Up Robust Features) classifier
 */
public class PatternMatcher extends ImageManipulator {

    // Template Images
    private Mat redBall;
    private Mat pinkDot;

    public PatternMatcher() {

        System.out.println("Loading template images...");

        redBall = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/ball_red.png");
        pinkDot = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/pink_dot.png");

        Imgproc.cvtColor(redBall, redBall, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(pinkDot, pinkDot, Imgproc.COLOR_BGR2HSV);
    }

    @Override
    protected Mat run(Mat image) {
        Mat outputMat = ImageManipulationPipeline.getInstance().undistortImage.catchMat();

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
        findSingleMatch(image, pinkDot, outputMat, new Scalar(203,192,255));
        findSingleMatch(image, redBall, outputMat, new Scalar(0,0,255));
        return outputMat;
    }

    private void findMatches(Mat image, Mat temp, Mat out, Scalar colour) {
        Mat result = new Mat();
        Imgproc.matchTemplate(image, temp, result, Imgproc.TM_CCOEFF);


        double thresh = 0.7;
        Imgproc.threshold(result, result, thresh, 1., Imgproc.THRESH_BINARY);


        Mat resb = new Mat();
        result.convertTo(resb, CvType.CV_8U, 255);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(resb, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i=0; i<contours.size(); ++i)
        {
            Mat mask = new Mat(result.rows(), result.cols(), 0);
            Imgproc.drawContours(mask, contours, i, new Scalar(255), Core.FILLED);

            Core.minMaxLoc(result, mask);

//            rectangle(img, Rect(max_point.x, max_point.y, templ.cols, templ.rows), Scalar(0,255,0), 2);
        }
    }

    private void findSingleMatch(Mat image, Mat temp, Mat out, Scalar colour) {
        int matcherType = Imgproc.TM_CCOEFF;
        Mat result = new Mat();

        Imgproc.matchTemplate(image, temp, result, matcherType);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult mmr = minMaxLoc(result);
        Point matchLocationTL;

//        if (matcherType  == Imgproc.TM_SQDIFF || matcherType == Imgproc.TM_SQDIFF_NORMED)
//            matchLocationTL = mmr.minLoc;
//        else
        matchLocationTL = mmr.maxLoc;

        Point matchLocationBR = new Point(matchLocationTL.x + temp.cols(),
                matchLocationTL.y + temp.rows());

        if (isDisplayed)
            Imgproc.rectangle(out, matchLocationTL, matchLocationBR, colour );
    }
}