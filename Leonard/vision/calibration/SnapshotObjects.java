package vision.calibration;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import vision.detection.ImageManipulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 16/02/17.
 * Segment objects from image
 */
public class SnapshotObjects extends JButton implements ChangeListener {

    private ImageManipulator catcher;
    private ImageManipulator masker;

    public SnapshotObjects(ImageManipulator imgToSegment, ImageManipulator mask) {
        catcher = imgToSegment;
        masker = mask;
        this.addChangeListener(this);
        this.setText("Snapshot Objects");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Mat img = catcher.catchMat();
        Mat mask = masker.catchMat();

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        int count = 0;
        for (MatOfPoint mop : contours) {
            Rect boundingBox = Imgproc.boundingRect(mop);
            Mat out = new Mat(img, boundingBox);
            Imgcodecs.imwrite("Leonard/vision/calibration/pre_saved_values/templates/img" + count + ".png", out);
            count++;
        }

    }
}
