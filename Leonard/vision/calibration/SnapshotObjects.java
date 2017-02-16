package vision.calibration;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import vision.detection.ImageManipulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.findNonZero;

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

//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(mask, contours, hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//        Mat out = new Mat();
//        findNonZero(mask, out);
//
//        int count = 0;
//        for (int i = 0; i < 3; i++) {
//            findNonZero(mask, 1);
//            img.copyTo(out, mop);
//            Imgcodecs.imwrite("vision/calibration/pre_saved_values/templates/file" + count + ".png"
//                    , img);
//            count++;
//        }
    }
}
