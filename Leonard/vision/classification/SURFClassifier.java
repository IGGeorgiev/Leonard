package vision.classification;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import vision.ImageManipulationPipeline;
import vision.detection.ImageManipulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 19/02/17.
 * An implementation of the SIFT/SURF (Sped Up Robust Features) classifier
 */
public class SURFClassifier extends ImageManipulator {

    // Template Images
    private Mat yellowGreen;
    private Mat yellowPink;
    private Mat blueGreen;
    private Mat bluePink;
    private Mat redBall;
    private Mat blueBall;

    // Template Image Key Points
    private MatOfKeyPoint ygKeyPoints = new MatOfKeyPoint();
    private MatOfKeyPoint ypKeyPoints = new MatOfKeyPoint();
    private MatOfKeyPoint bgKeyPoints = new MatOfKeyPoint();
    private MatOfKeyPoint bpKeyPoints = new MatOfKeyPoint();
    private MatOfKeyPoint rbKeyPoints = new MatOfKeyPoint();
    private MatOfKeyPoint bbKeyPoints = new MatOfKeyPoint();

    // Template Image Key Points Descriptions
    private Mat ygDesc = new Mat();
    private Mat ypDesc = new Mat();
    private Mat bgDesc = new Mat();
    private Mat bpDesc = new Mat();
    private Mat rbDesc = new Mat();
    private Mat bbDesc = new Mat();

    // Feature Extractor
    private FeatureDetector featureDetector;

    // Description Extractor
    private DescriptorExtractor descriptionExtractor;

    // Description Matcher
    private DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

    public SURFClassifier() {

        System.out.println("Loading template images...");

        yellowGreen = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/yellow_green.png");
        yellowPink = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/yellow_pink.png");
        blueGreen = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/blue_green.png");
        bluePink = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/blue_pink.png");
        redBall = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/ball_red.png");
        blueBall = Imgcodecs.imread("Leonard/vision/calibration/pre_saved_values/templates/ball_blue.png");

        Imgproc.cvtColor(redBall, redBall, Imgproc.COLOR_BGR2HSV);
        // Detect key points of templates
        featureDetector = FeatureDetector.create(FeatureDetector.SURF);

//        ygKeyPoints = getAllCentralPoints(yellowGreen);
//        ypKeyPoints = getAllCentralPoints(yellowPink);
//        rbKeyPoints = getAllCentralPoints(redBall);

//        featureDetector.detect(yellowGreen, ygKeyPoints);
//        featureDetector.detect(yellowPink, ypKeyPoints);
//        featureDetector.detect(blueGreen, bgKeyPoints);
//        featureDetector.detect(bluePink, bpKeyPoints);
        featureDetector.detect(redBall, rbKeyPoints);
//        featureDetector.detect(blueBall, bbKeyPoints);
        // Describe key points

        descriptionExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

//        descriptionExtractor.compute(yellowGreen, ygKeyPoints, ygDesc);
//        descriptionExtractor.compute(yellowPink, ypKeyPoints, ypDesc);
//        descriptionExtractor.compute(blueGreen, bgKeyPoints, bgDesc);
//        descriptionExtractor.compute(bluePink, bpKeyPoints, bpDesc);
        descriptionExtractor.compute(redBall, rbKeyPoints, rbDesc);

//        descriptionExtractor.compute(blueBall, bbKeyPoints, bbDesc);
    }

    @Override
    protected Mat run(Mat image) {
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
        featureDetector.detect(image, keyPoints);
        Mat kpDesc = new Mat();
        descriptionExtractor.compute(image, keyPoints, kpDesc);

        Mat outputMat = ImageManipulationPipeline.getInstance().undistortImage.catchMat();
        // Catch in case the key points of interest are much less than the ones in the query
        if (keyPoints.size().area() > ygKeyPoints.size().area()) {
//            findMatches(keyPoints, ygKeyPoints, kpDesc, ygDesc, outputMat, yellowGreen);
//        findMatches(keyPoints, ypKeyPoints, kpDesc, ypDesc, outputMat, yellowPink);
            findMatchTest(image, redBall, outputMat);
        }
        return outputMat;
    }

    private void findMatchTest(Mat image, Mat temp, Mat out) {
        int matcherType = Imgproc.TM_CCOEFF;
        int result_rows = image.rows() - temp.rows() + 1;
        int result_cols = image.cols() - temp.cols() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(image, temp, result, matcherType);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLocationTL = mmr.maxLoc;
        Point matchLocationBR = new Point(matchLocationTL.x + temp.cols(),
                matchLocationTL.y + temp.rows());

        Imgproc.rectangle(out, matchLocationTL, matchLocationBR, new Scalar(0,255) );
    }

    private void findMatches(
            MatOfKeyPoint pitchKeyPoints, MatOfKeyPoint objectKeyPoints,
            Mat pitchKPDesc, Mat objectKPDesc,
            Mat originalImage, Mat templateImage) {

        // Find Matching clusters
        LinkedList<MatOfDMatch> matches = new LinkedList<>();
        descriptorMatcher.knnMatch(objectKPDesc, pitchKPDesc, matches, 2);

        // Determine good matches
        LinkedList<DMatch> goodMatchesList = new LinkedList<>();

        float nndrRatio = 0.8f;

        for (MatOfDMatch m : matches) {
            DMatch[] dMatchArray = m.toArray();
            DMatch m1 = dMatchArray[0];
            DMatch m2 = dMatchArray[1];


            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);
            }
        }


        System.out.println("Key Points: " + pitchKeyPoints.size() + " " + objectKeyPoints.size());
        System.out.println("Matches found: " + matches.size() + " " + goodMatchesList.size());


        // Check if object has been located
        if (goodMatchesList.size() >= 1) {

            System.out.println("Object Found!");

            if (this.isDisplayed) {
                List<KeyPoint> objectKeyPointList = objectKeyPoints.toList();
                List<KeyPoint> pitchKeyPointList = pitchKeyPoints.toList();

                // Extract Point objects from matches
                LinkedList<Point> objectPoints = new LinkedList<>();
                LinkedList<Point> pitchPoints = new LinkedList<>();

                for (DMatch m : goodMatchesList) {
                    objectPoints.addLast(objectKeyPointList.get(m.queryIdx).pt);
                    pitchPoints.addLast(pitchKeyPointList.get(m.trainIdx).pt);
                }


                for (DMatch m : goodMatchesList) {
                    Imgproc.drawMarker(originalImage, pitchPoints.get(m.imgIdx), new Scalar(0, 255, 0));
                }

                // Convert to a MatOfPoint2f
                MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
                objMatOfPoint2f.fromList(objectPoints);
                MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
                scnMatOfPoint2f.fromList(pitchPoints);

                Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f);

                Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
                Mat pitch_corners = new Mat(4, 1, CvType.CV_32FC2);

                obj_corners.put(0, 0, 0, 0);
                obj_corners.put(1, 0, templateImage.cols(), 0);
                obj_corners.put(2, 0, templateImage.cols(), templateImage.rows());
                obj_corners.put(3, 0, 0, templateImage.rows());

                if (homography.size().area() != 0) {
                    // Transfer corners using homography matrix
                    Core.perspectiveTransform(obj_corners, pitch_corners, homography);

                    // Draw Lines on image
                    Imgproc.line(originalImage, new Point(pitch_corners.get(0, 0)), new Point(pitch_corners.get(1, 0)), new Scalar(0, 255, 0), 10);
                    Imgproc.line(originalImage, new Point(pitch_corners.get(1, 0)), new Point(pitch_corners.get(2, 0)), new Scalar(0, 255, 0), 10);
                    Imgproc.line(originalImage, new Point(pitch_corners.get(2, 0)), new Point(pitch_corners.get(3, 0)), new Scalar(0, 255, 0), 10);
                    Imgproc.line(originalImage, new Point(pitch_corners.get(3, 0)), new Point(pitch_corners.get(0, 0)), new Scalar(0, 255, 0), 10);
                }
            }
        }
    }

    private MatOfKeyPoint getAllCentralPoints(Mat img) {

        ArrayList<KeyPoint> kps = new ArrayList<>();
        for (int i = 9; i<img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                kps.add(new KeyPoint((float) j, (float) i, 1));
            }
        }

        KeyPoint[] kpsArray = new KeyPoint[kps.size()];
        kps.toArray(kpsArray);
        return new MatOfKeyPoint(kpsArray);
    }
}
