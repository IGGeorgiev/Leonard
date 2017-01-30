package vision;

import org.opencv.core.Core;
import vision.calibration.CalibrateEmptyPitchButton;
import vision.capture.VideoCapture;
import vision.object_recognition.BinaryImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    private VisionGUI() {
        initGUI();
    }


    private void initGUI() {
        frame = new JFrame();
        pane = new JPanel(new GridLayout(2,2));
        vcLabel = new VideoCapture();
        vcLabel.setSize(new Dimension(width, height));
        biLabel = new BinaryImage();
        biLabel.setSize(new Dimension(width / 2, height / 2));
        vcLabel.addFrameReceivedListener(biLabel);

        CalibrateEmptyPitchButton createNormalizedRGBButton = new CalibrateEmptyPitchButton(vcLabel);

        frame.setTitle("MainVideoFeed");
        Container c = frame.getContentPane();
        c.add(pane);

        pane.add(vcLabel);
        pane.add(biLabel);
        pane.add(createNormalizedRGBButton);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setSize(width, height);
//        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
    }



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
