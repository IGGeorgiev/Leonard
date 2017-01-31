package vision;

import org.opencv.core.Core;
import vision.calibration.CalibrateEmptyPitchButton;
import vision.capture.VideoCapture;
import vision.object_recognition.BinaryImage;
import vision.object_recognition.ErodedAndDilatedImage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static vision.capture.VideoCapture.height;
import static vision.capture.VideoCapture.width;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Graphical User Interface for the Vision System
 */
public class VisionGUI extends WindowAdapter {

    private JFrame frame;
    private JPanel pane;
    private VideoCapture vcLabel;
    private BinaryImage biLabel;
    private ErodedAndDilatedImage eadLabel;
    private JPanel optionsPane;
    private JSlider thresholdSlider;
    private JSlider rthresholdSlider;
    private JSlider gthresholdSlider;
    private JSlider bthresholdSlider;
    private JSlider gaussianBlurSlider;
    private JSlider erosionSlider;
    private JSlider dilationSlider;

    private VisionGUI() {
        initGUI();
    }

    /**
     * UI class to add named sliders
     */
    private class TitledSlider extends JPanel {
        TitledSlider(String sliderTitle, JSlider slider) {
            super();
            JLabel title = new JLabel();
            title.setText(sliderTitle);
            this.add(title);
            this.add(slider);
        }
    }


    private void initGUI() {
        frame = new JFrame();
        pane = new JPanel(new GridLayout(2,2));

        setupOptionsPane();

        vcLabel = new VideoCapture();
        vcLabel.setSize(new Dimension(width, height));

        biLabel = new BinaryImage(rthresholdSlider, gthresholdSlider, bthresholdSlider);
        biLabel.setSize(new Dimension(width / 2, height / 2));

        eadLabel = new ErodedAndDilatedImage();
        eadLabel.setSize(new Dimension(width / 2, height / 2));

        vcLabel.addFrameReceivedListener(biLabel);
        biLabel.addFrameReceivedListener(eadLabel);

        frame.setTitle("MainVideoFeed");
        Container c = frame.getContentPane();
        c.add(pane);

        // Construct main scene
        pane.add(vcLabel);
        pane.add(biLabel);
        pane.add(optionsPane);
        pane.add(eadLabel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setSize(width, height);
//        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
    }

    private void setupOptionsPane() {
        optionsPane = new JPanel();
        optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));

        thresholdSlider = new JSlider(0, 100, 0);
        thresholdSlider.addChangeListener(new BinaryImage.ThresholdChangeListener());

        rthresholdSlider = new JSlider(0, 100, 0);
        rthresholdSlider.addChangeListener(new BinaryImage.RThresholdChangeListener());

        gthresholdSlider = new JSlider(0, 100, 0);
        gthresholdSlider.addChangeListener(new BinaryImage.GThresholdChangeListener());

        bthresholdSlider = new JSlider(0, 100, 0);
        bthresholdSlider.addChangeListener(new BinaryImage.BThresholdChangeListener());

        gaussianBlurSlider = new JSlider(1, 11, 3);
        gaussianBlurSlider.setMajorTickSpacing(2);
        gaussianBlurSlider.setSnapToTicks(true);
        gaussianBlurSlider.setPaintTicks(true);
        gaussianBlurSlider.addChangeListener(new BinaryImage.GaussianBlurChangeListener());

        erosionSlider = new JSlider(1, 10, 3);
        erosionSlider.setSnapToTicks(true);
        erosionSlider.setPaintTicks(true);
        erosionSlider.addChangeListener(new ErodedAndDilatedImage.ErosionChangeListener());


        dilationSlider = new JSlider(1, 10, 3);
        dilationSlider.setSnapToTicks(true);
        dilationSlider.setPaintTicks(true);
        dilationSlider.addChangeListener(new ErodedAndDilatedImage.DialationChangeListener());

        CalibrateEmptyPitchButton createNormalizedRGBButton = new CalibrateEmptyPitchButton(vcLabel);

        // Add buttons and sliders
        optionsPane.add(createNormalizedRGBButton);
        optionsPane.add(new TitledSlider("Threshold: ", thresholdSlider));
        optionsPane.add(new TitledSlider("Threshold R:", rthresholdSlider));
        optionsPane.add(new TitledSlider("Threshold G:", gthresholdSlider));
        optionsPane.add(new TitledSlider("Threshold B:", bthresholdSlider));
        optionsPane.add(new TitledSlider("GaussianBlur: ", gaussianBlurSlider));
        optionsPane.add(new TitledSlider("Dilation: ", dilationSlider));
        optionsPane.add(new TitledSlider("Erosion: ", erosionSlider));

//        loadValues();
    }

//    private void loadValues() {
//        File saveFile = new File("Leonard/vision/calibration/pre_saved_values/saveFile");
//        try {
//            if (saveFile.exists()) {
//                Reader reader = new FileReader(saveFile);
//                reader.read();
//            }
//        } catch (IOException io) {
//            System.out.println("No Save File found!");
//        }
//    }


    public void windowClosing(WindowEvent e) {

        vcLabel.cleanupCapture();
        frame.dispose();
        System.out.println("exiting!!!");
    }



    public static void main(String args[]) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VisionGUI();
            }
        });
    }
}
