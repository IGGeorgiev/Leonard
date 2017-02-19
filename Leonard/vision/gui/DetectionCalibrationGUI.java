package vision.gui;

import vision.calibration.CalibrateEmptyPitchButton;
import vision.calibration.SnapshotObjects;
import vision.capture.VideoCapture;
import vision.detection.*;
import vision.detection.manipulators.ApplyBinaryMask;
import vision.detection.manipulators.DilateImage;
import vision.detection.manipulators.UndistortImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

import static vision.detection.DetectionPropertiesManager.loadValues;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Graphical User Interface for the Vision System
 */
public class DetectionCalibrationGUI extends JPanel {

    private JPanel optionsPane;

    private ImageManipulationPipeline controller = ImageManipulationPipeline.getInstance();
    private LinkedList<ImageManipulator> pipeline = controller.pipeline;

    // Note - entry point to the pipeline is always the video feed
    public VideoCapture videoFeed = controller.videoCapture;

    public DetectionCalibrationGUI() {
        super(new GridLayout(2,2));
        optionsPane = new JPanel();
        optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));
        optionsPane.setBorder(new EmptyBorder(20,20,20,20));

        chooseDisplays();

        this.add(optionsPane);
    }


    private void chooseDisplays() {
        ArrayList<Component> displayQueue = new ArrayList<>();
        ArrayList<Component> optionsPanelDisplay = new ArrayList<>();

        // Choose what to display here
        for (ImageManipulator i : pipeline) {
//            if (i instanceof VideoCapture)
//                displayQueue.add(i.getDisplay());
            if (i instanceof UndistortImage)
                displayQueue.add(i.getDisplay());
//            if (i instanceof NormalizeImage)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof BackgroundSubtractionThreshold)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof GaussianBlurImage)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof ErodeImage)
//                displayQueue.add(i.getDisplay());
            if (i instanceof DilateImage)
                displayQueue.add(i.getDisplay());
            if (i instanceof ApplyBinaryMask)
                displayQueue.add(i.getDisplay());
            if (i instanceof ImageManipulatorWithOptions)
                optionsPanelDisplay.add(((ImageManipulatorWithOptions) i).getModificationGUI());
        }

        // Load last saved values
        loadValues();

        // Create View
        for (Component c : displayQueue)
            this.add(c);
        for (Component c : optionsPanelDisplay)
            optionsPane.add(c);
        optionsPane.add(new CalibrateEmptyPitchButton(controller.hsvImage));
        optionsPane.add(new SnapshotObjects(controller.undistortImage, controller.dilateImage));
    }



//    public static void main(String args[]) {
//
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        SwingUtilities.invokeLater(DetectionCalibrationGUI::new);
//    }
}
