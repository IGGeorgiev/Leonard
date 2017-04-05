package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import vision.objectRecognition.detection.ImageManipulatorWithOptions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

/**
 * MOG Background subtraction with a history of a static background image
 * Created by Ivan Georgiev (s1410984) on 06/03/17.
 */
public class BackgroundSubtractionMOG extends ImageManipulatorWithOptions implements ActionListener {
    private int history = 200;
    private BackgroundSubtractorMOG2 subtractor;
    private JPanel buttonHolder = new JPanel();
    private JButton staticLearn = new JButton("Static Learn");
    private JButton dynamicLearn = new JButton("Dynamic Learn");
    private JButton setBackgroundPath = new JButton("Set Background Path");
    private JLabel calibrating = new JLabel("Calibrated");
    private final JFileChooser backgroundSetter = new JFileChooser(".");
    private int dynamicLearnCount = history;
    static String BACKGROUND_PATH = "src/vision/objectRecognition/calibration/pre_saved_values/empty_pitch_norm.png";
    final static String BACKGROUND_PATH_PREF = "BackgroundPath";

    public BackgroundSubtractionMOG() {
        staticLearn.addActionListener(this);
        dynamicLearn.addActionListener(this);
        buttonHolder.add(staticLearn);
        buttonHolder.add(dynamicLearn);
        buttonHolder.add(calibrating);
//        buttonHolder.add(setBackgroundPath);
        subtractor = Video.createBackgroundSubtractorMOG2(history, 64, true);
        staticLearn();
    }

    private void staticLearn() {
        Mat img = Imgcodecs.imread(BACKGROUND_PATH);
        Mat fgmask = new Mat();
        for(int i = 0; i < history; i++) {
            subtractor.apply(img,fgmask, 1);
        }
    }

    private void dynamicLearn() {
        this.dynamicLearnCount = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (button.equals(staticLearn)) {
            dynamicLearnCount = history;
            staticLearn();
        } else if (button.equals(dynamicLearn)){
            dynamicLearn();
        } else if (button.equals(setBackgroundPath)) {
            int retVal = backgroundSetter.showOpenDialog(new JFrame("Choose a File"));
            if (retVal != JFileChooser.APPROVE_OPTION) {
                File file = backgroundSetter.getSelectedFile();
                BACKGROUND_PATH = file.getPath();
            }
        }
    }

    @Override
    protected Mat run(Mat input) {
        Mat out = new Mat();
        if (dynamicLearnCount >= history) {
            calibrating.setText("Calibrated");
            subtractor.apply(input, out, 0);
        } else {
            calibrating.setText("Calibrating...");
            dynamicLearnCount++;
            subtractor.apply(input, out, 0.02);
        }
        return out;
    }

    @Override
    public Component getModificationGUI() {
        return buttonHolder;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(BACKGROUND_PATH_PREF, BACKGROUND_PATH);
    }

    @Override
    public void loadModificationSettings(Properties prop) {
        BACKGROUND_PATH = prop.getProperty(BACKGROUND_PATH_PREF, BACKGROUND_PATH);
        staticLearn();
    }

}
