package vision.rawInput;

import org.opencv.core.Mat;

/**
 * Created by Ivan Georgiev (s1410984) on 06/02/17.
 */

public interface MatFrameListener {
    void onFrameReceived(Mat image, long time);
}


