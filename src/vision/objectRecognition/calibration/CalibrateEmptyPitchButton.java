package vision.objectRecognition.calibration;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import vision.objectRecognition.detection.ImageManipulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Class to acquire a normalized image of an empty pitch
 */
public class CalibrateEmptyPitchButton extends JButton implements ActionListener {

    private ImageManipulator manipulator;

    public CalibrateEmptyPitchButton(ImageManipulator ni) {
        super();
        manipulator = ni;
        this.setText("Calibrate Empty Field");
        this.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        Mat img = manipulator.catchMat();
        JFileChooser fileChooser = new JFileChooser(".");
        if (fileChooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Imgcodecs.imwrite(file.getAbsolutePath(),
                    img);
        }
    }
}
