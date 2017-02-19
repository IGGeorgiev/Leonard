package vision;

import org.opencv.core.Core;
import vision.capture.VideoCapture;
import vision.gui.ClassifierGUI;
import vision.gui.DetectionCalibrationGUI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static vision.detection.DetectionPropertiesManager.saveValues;
import static vision.capture.VideoCapture.height;
import static vision.capture.VideoCapture.width;

/**
 * Created by Ivan Georgiev (s1410984) on 19/02/17.
 * Launcher for the vision system
 */
public class Launcher extends WindowAdapter {

    private JFrame frame;
    private JTabbedPane pane;

    private VideoCapture videoFeed;

    private Launcher() {

        frame = new JFrame();
        pane = new JTabbedPane();
        DetectionCalibrationGUI detectionGUI = new DetectionCalibrationGUI();
//        ClassifierGUI classifierGUI = new ClassifierGUI();

        videoFeed = detectionGUI.videoFeed;

//        pane.addTab("Classifier", classifierGUI);
        pane.addTab("Detection", detectionGUI);

        frame.add(pane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setSize(width, height);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );

    }

    public void windowClosing(WindowEvent e) {
        saveValues();
        videoFeed.cleanupCapture();
        frame.dispose();

        System.out.println("Good Bye!");
    }

    public static void main(String... args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SwingUtilities.invokeLater(Launcher::new);
    }
}
