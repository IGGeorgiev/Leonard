package vision.rawInput;

import java.awt.*;
import java.awt.event.WindowAdapter;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import vision.gui.SDPConsole;
/**
 * This class demonstrates how to tik a simple push-mode capture.
 * It starts the capture and display the video stream in a JLabel
 * @author gilles
 *
 */
public class Video extends WindowAdapter {
	private static int      width = 640, height = 480;

    private VideoDevice     videoDevice;
    private FrameGrabber    frameGrabber;


    /**
     * Builds a WebcamViewer object
     * @throws V4L4JException if any parameter if invalid
     */
    public Video(CaptureCallback cc, String port, int channel, short standard){
            // Initialise video device and frame grabber
            try {
            	videoDevice = new VideoDevice(port);
                ImageFormat imageFormat = videoDevice.getDeviceInfo().getFormatList().getJPEGEncodableFormat(0);
                frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, standard, 100, imageFormat);
                frameGrabber.setCaptureCallback(cc);
                width = frameGrabber.getWidth();
                height = frameGrabber.getHeight();
            } catch (V4L4JException e1) {
            	SDPConsole.message("The video device could not be created. Try changing the channel to a different number.", (Component)cc);
                // cleanup and exit
                destroyCamera();
                return;
            }
            
            // start capture
            try {
                    frameGrabber.startCapture();
            } catch (V4L4JException e){
            	SDPConsole.message("The video capture could not be started. Try changing the channel to a different number.", (Component)cc);
            	return;
            }
    }
    
    /**
     * this method stops the capture and releases the frame grabber and video device
     */
    public void destroyCamera() {
            try {
                	// release the frame grabber and video device
                    frameGrabber.stopCapture();
                    videoDevice.releaseFrameGrabber();
                    videoDevice.release();
            } catch (Exception ex) {
                    // the frame grabber may be already stopped, so we just ignore
                    // any exception and simply continue.
            }
    }
}