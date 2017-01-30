package vision.capture;

import java.awt.image.BufferedImage;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Interface to send newly received frames from video device.
 */
public interface FrameReceivedListener {
    void onFrameReceived(BufferedImage image);
}
