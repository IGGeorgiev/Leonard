package vision.capture;


import au.edu.jcu.v4l4j.*;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import vision.ImageManipulator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 28/01/17.
 *
 * Most of this is copied from the v4l4j example on github @ https://gist.github.com/gustavohenrique/1342769
 */
public class VideoCapture extends ImageManipulator implements CaptureCallback {
    public static int width = 640, height = 480;
    private static int std = V4L4JConstants.STANDARD_PAL, channel = 2;

    private static String device = "/dev/video0";
    private VideoDevice videoDevice;
    private FrameGrabber frameGrabber;

    private BufferedImage manipulatedImage;


    private List<FrameReceivedListener> frameListeners = new ArrayList<>();


    public VideoCapture() {
        initFrameGrabber();

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

    @Override
    public void nextFrame(VideoFrame frame) {
        // This method is called when a new frame is ready.
        // Don't forget to recycle it when done dealing with the frame.

        manipulatedImage = frame.getBufferedImage();

        this.onFrameReceived(manipulatedImage);

        // recycle the frame
        frame.recycle();
    }

    @Override
    protected BufferedImage run(BufferedImage input) {
        return manipulatedImage;
    }
}
