package vision.objectRecognition.calibration;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import vision.objectRecognition.detection.ImageManipulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 16/02/17.
 * Segment objects from image
 */
public class SnapshotObjects extends JButton implements ActionListener {

    private ImageManipulator catcher;
    private ImageManipulator masker;

    public SnapshotObjects(ImageManipulator imgToSegment, ImageManipulator mask) {
        catcher = imgToSegment;
        masker = mask;
        this.addActionListener(this);
        this.setText("Snapshot Objects");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Mat img = catcher.catchMat();
        Mat mask = masker.catchMat();

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() < 10) {
            int count = 0;
            for (MatOfPoint mop : contours) {
                Rect boundingBox = Imgproc.boundingRect(mop);
                Mat out = new Mat(img, boundingBox);
                Imgcodecs.imwrite("src/vision/objectRecognition/calibration/pre_saved_values/templates/img" + count + ".png", out);
                count++;
            }
        }
    }
}
