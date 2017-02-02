package vision;

import org.opencv.core.Core;
import vision.calibration.CalibrateEmptyPitchButton;
import vision.capture.VideoCapture;
import vision.detection.BinaryImage;
import vision.detection.ErodedAndDilatedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static vision.DetectionPropertiesManager.loadValues;
import static vision.DetectionPropertiesManager.saveValues;
import static vision.capture.VideoCapture.height;
import static vision.capture.VideoCapture.width;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Graphical User Interface for the Vision System
 */
public class DetectionCalibrationGUI extends WindowAdapter {

    private JFrame frame;
    private JPanel pane;
    private VideoCapture vcLabel;
    private BinaryImage biLabel;
    private ErodedAndDilatedImage eadLabel;
    private JPanel optionsPane;
    private JSlider thresholdSlider;

    static JSlider RThresholdSlider;
    static JSlider BThresholdSlider;
    static JSlider GThresholdSlider;
    static JSlider gaussianBlurSlider;
    static JSlider erosionSlider;
    static JSlider dilationSlider;

    public enum  PitchNames {
        STANDARD_OUTPUT, PITCH_0, PITCH_1
    }

    static JComboBox<PitchNames> pitchChooser;

    private DetectionCalibrationGUI() {
        initGUI();
    }

    /**
     * UI class to add named sliders
     */
    private class TitledComponent extends JPanel {
        TitledComponent(String sliderTitle, Component component) {
            super();
            JLabel title = new JLabel();
            title.setText(sliderTitle);
            this.add(title);
            this.add(component);
        }
    }


    private void initGUI() {
        frame = new JFrame();
        pane = new JPanel(new GridLayout(2,2));

        vcLabel = new VideoCapture();
        vcLabel.setSize(new Dimension(width, height));

        setupOptionsPane();

        biLabel = new BinaryImage(RThresholdSlider, BThresholdSlider, GThresholdSlider);
        biLabel.setSize(new Dimension(width / 2, height / 2));

        eadLabel = new ErodedAndDilatedImage();
        eadLabel.setSize(new Dimension(width / 2, height / 2));

        vcLabel.addFrameReceivedListener(biLabel);
        biLabel.addFrameReceivedListener(eadLabel);

        frame.setTitle("Detection Settings");
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
        optionsPane.setBorder(new EmptyBorder(10,10,10,10));
        optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));

        thresholdSlider = new JSlider(0, 100, 0);
        thresholdSlider.addChangeListener(new BinaryImage.ThresholdChangeListener());

        RThresholdSlider = new JSlider(0, 100, 0);
        RThresholdSlider.addChangeListener(new BinaryImage.RThresholdChangeListener());

        BThresholdSlider = new JSlider(0, 100, 0);
        BThresholdSlider.addChangeListener(new BinaryImage.GThresholdChangeListener());

        GThresholdSlider = new JSlider(0, 100, 0);
        GThresholdSlider.addChangeListener(new BinaryImage.BThresholdChangeListener());

        pitchChooser = new JComboBox<>(PitchNames.values());
        pitchChooser.addActionListener(vcLabel.pitchChosenListener);

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
        optionsPane.add(new TitledComponent("Threshold: ", thresholdSlider));
        optionsPane.add(new TitledComponent("Threshold R:", RThresholdSlider));
        optionsPane.add(new TitledComponent("Threshold B:", BThresholdSlider));
        optionsPane.add(new TitledComponent("Threshold G:", GThresholdSlider));
        optionsPane.add(new TitledComponent("GaussianBlur: ", gaussianBlurSlider));
        optionsPane.add(new TitledComponent("Dilation: ", dilationSlider));
        optionsPane.add(new TitledComponent("Erosion: ", erosionSlider));
        optionsPane.add(new TitledComponent("Pitch Chooser: ", pitchChooser));
        optionsPane.add(createNormalizedRGBButton);

        loadValues();
    }


    public void windowClosing(WindowEvent e) {
        saveValues();
        vcLabel.cleanupCapture();
        frame.dispose();
        System.out.println("exiting!!!");
    }



    public static void main(String args[]) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SwingUtilities.invokeLater(DetectionCalibrationGUI::new);
    }
}
