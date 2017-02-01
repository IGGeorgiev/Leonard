package vision.capture;


import au.edu.jcu.v4l4j.*;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.DetectionCalibrationGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToImage;

/**
 * Created by Ivan Georgiev (s1410984) on 28/01/17.
 *
 * Most of this is copied from the v4l4j example on github @ https://gist.github.com/gustavohenrique/1342769
 */
public class VideoCapture extends JLabel implements CaptureCallback {
    public static int width = 640, height = 480;
    private static int std = V4L4JConstants.STANDARD_PAL, channel = 2;

    public static PitchChosenListener pitchChosenListener;

    private static String device = "/dev/video0";
    private VideoDevice videoDevice;
    private FrameGrabber frameGrabber;

    private BufferedImage currentImage;

    private DetectionCalibrationGUI.PitchNames pitchChosen =
            DetectionCalibrationGUI.PitchNames.STANDARD_OUTPUT;

    private Mat pitch0NewCameraMatrix;
    private Mat pitch0DistCoeffs;
    private Mat pitch0CameraMatrix;

    private Mat pitch1NewCameraMatrix;
    private Mat pitch1DistCoeffs;
    private Mat pitch1CameraMatrix;


    private List<FrameReceivedListener> frameListeners = new ArrayList<>();

    private class PitchChosenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox) e.getSource();
            pitchChosen = (DetectionCalibrationGUI.PitchNames) box.getSelectedItem();
        }
    }

    public VideoCapture() {
        initFrameGrabber();

        Mat[] matrices = DistortionConstantsLoader.loadMatrices();
        pitch0NewCameraMatrix = matrices[0];
        pitch0DistCoeffs = matrices[1];
        pitch0CameraMatrix = matrices[2];

        pitch1NewCameraMatrix = matrices[3];
        pitch1DistCoeffs = matrices[4];
        pitch1CameraMatrix = matrices[5];

        // start capture
        try {
            if (frameGrabber != null)
                frameGrabber.startCapture();
        }
        catch (V4L4JException e) {
            System.err.println("Error starting the capture");
            e.printStackTrace();
        }
    }

    public void addFrameReceivedListener(FrameReceivedListener frl) {
        frameListeners.add(frl);
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
//            e1.printStackTrace();

            // cleanup and exit
            cleanupCapture();
        }
    }


    public void cleanupCapture() {
        if (frameGrabber != null && videoDevice != null) {
            try {
                frameGrabber.stopCapture();
            } catch (StateException ex) {
                // the frame grabber may be already stopped, so we just ignore
                // any exception and simply continue.
            }

            // release the frame grabber and video device
            videoDevice.releaseFrameGrabber();
            videoDevice.release();
        }
    }

    @Override
    public void exceptionReceived(V4L4JException e) {
        // This method is called by v4l4j if an exception
        // occurs while waiting for a new frame to be ready.
        // The exception is available through e.getCause()
        e.printStackTrace();
    }

    public BufferedImage getCurrentImage() {
        if (currentImage != null) {
            return currentImage;
        }
        System.out.println("ERROR. Bad Frame");
        return null;
    }

    @Override
    public void nextFrame(VideoFrame frame) {
        // This method is called when a new frame is ready.
        // Don't forget to recycle it when done dealing with the frame.

        currentImage = frame.getBufferedImage();

        Mat undistorted = new Mat();
        switch (pitchChosen) {
            case PITCH_0:
                Imgproc.undistort(
                        imageToMat(frame.getBufferedImage()),
                        undistorted,
                        pitch0CameraMatrix,
                        pitch0DistCoeffs,
                        pitch0NewCameraMatrix
                );

                currentImage = matToImage(undistorted);
                break;

            case PITCH_1:
                Imgproc.undistort(
                        imageToMat(frame.getBufferedImage()),
                        undistorted,
                        pitch1CameraMatrix,
                        pitch1DistCoeffs,
                        pitch1NewCameraMatrix
                );

                currentImage = matToImage(undistorted);
                break;
            default:
                // Do nothing
        }

        // Send to all frame listeners
        for (FrameReceivedListener l : frameListeners) {
            new Thread(() -> l.onFrameReceived(currentImage)).run();
        }

        // draw the new frame onto the JLabel
        this.getGraphics().drawImage(currentImage, 0, 0, width, height, null);

        // recycle the frame
        frame.recycle();
    }
}
