package vision;


import au.edu.jcu.v4l4j.*;

import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import vision.calibration.CalibrateEmptyPitchButton;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Created by s1410984 on 28/01/17.
 *
 * Most of this is copied from the v4l4j example on github @ https://gist.github.com/gustavohenrique/1342769
 */
public class VideoCapture extends WindowAdapter implements CaptureCallback {
    private static int width = 640, height = 480, std = V4L4JConstants.STANDARD_PAL, channel = 2;
    private static String device = "/dev/video0";
    private VideoDevice videoDevice;
    private FrameGrabber frameGrabber;
    private JLabel label;
    private JFrame frame;
    private JPanel pane;
    private BufferedImage currentImage;

    VideoCapture() {
        initFrameGrabber();


        // create and initialise UI
        initGUI();

        // start capture
        try {
            frameGrabber.startCapture();
        }
        catch (V4L4JException e) {
            System.err.println("Error starting the capture");
            e.printStackTrace();
        }
    }


    private void initFrameGrabber() {
        try {
            videoDevice = new VideoDevice(device);
            frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
            frameGrabber.setCaptureCallback(this);
            width = frameGrabber.getWidth();
            height = frameGrabber.getHeight();
            System.out.println("Starting capture at " + width + "x" + height);
        } catch (V4L4JException e1) {
            System.err.println("Error setting up capture");
            e1.printStackTrace();

            // cleanup and exit
            cleanupCapture();
        }
    }

    private void initGUI() {
        frame = new JFrame();
        pane = new JPanel(new BorderLayout(10,10));
        label = new JLabel();
        label.setMinimumSize(new Dimension(width, height));

        JButton button = new JButton("Capture");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        CalibrateEmptyPitchButton createNormalizedRGBButton = new CalibrateEmptyPitchButton(this);

        frame.setTitle("MainVideoFeed");
//        frame.add(label);
        frame.add(pane);
        pane.add(label, BorderLayout.NORTH);
        pane.add(createNormalizedRGBButton, BorderLayout.SOUTH);
//        frame.getContentPane().add(createNormalizedRGBButton, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setVisible(true);
        frame.setSize(width, height);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
    }

    private void cleanupCapture() {
        try {
            frameGrabber.stopCapture();
        }
        catch (StateException ex) {
            // the frame grabber may be already stopped, so we just ignore
            // any exception and simply continue.
        }

        // release the frame grabber and video device
        videoDevice.releaseFrameGrabber();
        videoDevice.release();
    }

    public void windowClosing(WindowEvent e) {
        cleanupCapture();
        frame.dispose();
        System.out.println("exiting!!!");
    }

    @Override
    public void exceptionReceived(V4L4JException e) {
        // This method is called by v4l4j if an exception
        // occurs while waiting for a new frame to be ready.
        // The exception is available through e.getCause()
        e.printStackTrace();
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    @Override
    public void nextFrame(VideoFrame frame) {
        // This method is called when a new frame is ready.
        // Don't forget to recycle it when done dealing with the frame.
        currentImage = frame.getBufferedImage();

        // draw the new frame onto the JLabel
        label.getGraphics().drawImage(currentImage, 0, 0, width, height, null);

        // recycle the frame
        frame.recycle();
    }


    public static void main(String args[]) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VideoCapture();
            }
        });
    }
}
