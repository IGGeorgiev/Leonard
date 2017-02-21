package vision;

import org.opencv.core.Core;
import vision.capture.VideoCapture;
import vision.classification.SURFClassifier;
import vision.gui.ClassifierGUI;
import vision.gui.DetectionCalibrationGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static vision.detection.DetectionPropertiesManager.saveValues;
import static vision.capture.VideoCapture.height;
import static vision.capture.VideoCapture.width;

/**
 * Created by Ivan Georgiev (s1410984) on 19/02/17.
 * Launcher for the vision system
 */
public class Launcher extends WindowAdapter implements ChangeListener{

    private JFrame frame;
    private JTabbedPane pane;

    private VideoCapture videoFeed;

    private final String CLASSIFIER_TAB = "Classifier";
    private final String DETECTION_TAB = "Detection";

    private DetectionCalibrationGUI detectionGUI;
    private ClassifierGUI classifierGUI;

    private Launcher() {

        frame = new JFrame();
        pane = new JTabbedPane();
        pane.addChangeListener(this);

        classifierGUI = new ClassifierGUI();
        detectionGUI = new DetectionCalibrationGUI();

        detectionGUI.hideAll();

        videoFeed = detectionGUI.videoFeed;

        pane.addTab(CLASSIFIER_TAB, classifierGUI);
        pane.addTab(DETECTION_TAB, detectionGUI);

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

    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int index = tabbedPane.getSelectedIndex();
        switch (tabbedPane.getTitleAt(index)) {
            case DETECTION_TAB:
                classifierGUI.hideAll();
                detectionGUI.showAll();
                break;
            case CLASSIFIER_TAB:
                detectionGUI.hideAll();
                classifierGUI.showAll();
                break;
        }
    }
}
