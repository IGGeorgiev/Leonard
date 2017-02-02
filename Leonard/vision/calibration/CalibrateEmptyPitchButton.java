package vision.calibration;

import org.opencv.imgcodecs.Imgcodecs;
import vision.detection.NormalizeImage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import static vision.utils.Converter.imageToMat;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Class to acquire a normalized image of an empty pitch
 */
public class CalibrateEmptyPitchButton extends JButton implements ActionListener {

    private NormalizeImage normalizer;

    public CalibrateEmptyPitchButton(NormalizeImage ni) {
        super();
        normalizer = ni;
        this.setText("Calibrate Empty Field");
        this.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        BufferedImage img = normalizer.catchFrame();
        Imgcodecs.imwrite("Leonard/vision/calibration/pre_saved_values/empty_pitch_norm.png",
                imageToMat(img));
    }
}
