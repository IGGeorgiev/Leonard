package vision.gui;

import vision.ImageManipulationPipeline;
import vision.calibration.CalibrateEmptyPitchButton;
import vision.calibration.SnapshotObjects;
import vision.capture.VideoCapture;
import vision.classification.PatternMatcher;
import vision.detection.*;
import vision.detection.manipulators.*;

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

    private ArrayList<ImageManipulator> displayedManipulators = new ArrayList<>();

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
            if (i instanceof GaussianBlurImage) {
                displayQueue.add(i.getDisplay());
                displayedManipulators.add(i);
            }
//            if (i instanceof NormalizeImage)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof BackgroundSubtractionThreshold)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof GaussianBlurImage)
//                displayQueue.add(i.getDisplay());
//            if (i instanceof ErodeImage)
//                displayQueue.add(i.getDis
            if (i instanceof DilateImage) {
                displayQueue.add(i.getDisplay());
                displayedManipulators.add(i);
            }
            if (i instanceof ApplyBinaryMask) {
                displayQueue.add(i.getDisplay());
                displayedManipulators.add(i);
            }
            if (i instanceof ImageManipulatorWithOptions)
                optionsPanelDisplay.add(((ImageManipulatorWithOptions) i).getModificationGUI());
        }

        // Load last saved values
        loadValues();

        // Create View
        displayQueue.forEach(this::add);
        optionsPanelDisplay.forEach((x) -> optionsPane.add(x));

        optionsPane.add(new CalibrateEmptyPitchButton(controller.hsvImage));
        optionsPane.add(new SnapshotObjects(controller.gaussianBlur, controller.dilateImage));
    }

    public void hideAll() {
        displayedManipulators.forEach(ImageManipulator::hideDisplay);
    }

    public void showAll() {displayedManipulators.forEach(ImageManipulator::getDisplay); }

//    public static void main(String args[]) {
//
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        SwingUtilities.invokeLater(DetectionCalibrationGUI::new);
//    }
}
