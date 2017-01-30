package vision.calibration;

import vision.capture.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static vision.utils.Normalizer.normalizeRGB;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Class to acquire a normalized image of an empty pitch
 */
public class CalibrateEmptyPitchButton extends JButton implements ActionListener {

    private VideoCapture videoFeed;

    public CalibrateEmptyPitchButton(VideoCapture vc) {
        super();
        videoFeed = vc;
        this.setText("Calibrate Empty Field");
        this.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        BufferedImage img = normalizeRGB(videoFeed.getCurrentImage());
        File file = new File("Leonard/vision/calibration/pre_saved_values/empty_pitch_norm.jpg");
        try {
            file.createNewFile();
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
