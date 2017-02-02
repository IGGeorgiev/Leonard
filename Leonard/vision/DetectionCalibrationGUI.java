package vision;

import org.opencv.core.Core;
import vision.calibration.CalibrateEmptyPitchButton;
import vision.capture.VideoCapture;
import vision.detection.GaussianBlurImage;
import vision.detection.UndistortImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;

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
    private JPanel optionsPane;

    private LinkedList<ImageManipulator> pipeline = ImageManipulationPipeline.getInstance().pipeline;

    // Note - entry point to the pipeline is always the video feed
    private VideoCapture videoFeed = (VideoCapture) pipeline.getFirst();

    private DetectionCalibrationGUI() {
        frame = new JFrame();
        pane = new JPanel(new GridLayout(2,2));
        optionsPane = new JPanel(new GridLayout(10,1));
        optionsPane.setBorder(new EmptyBorder(20,20,20,20));

        chooseDisplays();

        pane.add(optionsPane);
        frame.add(pane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setSize(width, height);
//        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
    }


    private void chooseDisplays() {
        ArrayList<Component> displayQueue = new ArrayList<>();
        ArrayList<Component> optionsPanelDisplay = new ArrayList<>();

        // Choose what to display here
        for (ImageManipulator i : pipeline) {
            if (i instanceof UndistortImage)
                displayQueue.add(i.getDisplay());
            if (i instanceof GaussianBlurImage)
                displayQueue.add(i.getDisplay());
            if (i instanceof ImageManipulatorWithOptions)
                optionsPanelDisplay.add(((ImageManipulatorWithOptions) i).getModificationGUI());
        }

        // Load last saved values
        loadValues();

        // Create View
        for (Component c : displayQueue)
            pane.add(c);
        for (Component c : optionsPanelDisplay)
            optionsPane.add(c);
        optionsPane.add(new CalibrateEmptyPitchButton(videoFeed));
    }


    public void windowClosing(WindowEvent e) {
        saveValues();

        videoFeed.cleanupCapture();

        frame.dispose();
        System.out.println("Good Bye!");
    }



    public static void main(String args[]) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SwingUtilities.invokeLater(DetectionCalibrationGUI::new);
    }
}
