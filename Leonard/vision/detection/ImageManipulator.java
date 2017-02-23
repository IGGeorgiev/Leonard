package vision.detection;

import org.opencv.core.Mat;
import vision.capture.MatFrameListener;
import vision.classification.PatternMatcher;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static vision.utils.Converter.matToBufferedImage;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * An abstract class to manage the pipeline of manipulators the input video goes through
 */
public abstract class ImageManipulator implements MatFrameListener {

    public MatFrameListener nextManipulator = null;
    public JLabel manipulatorDisplay = new JLabel();
    public boolean isDisplayed = false;
    public Mat manipulatedImage = null;

    public JLabel getDisplay(boolean setIsDisplayed) {
        if (manipulatorDisplay == null)
            manipulatorDisplay = new JLabel();
        isDisplayed = setIsDisplayed;
        return manipulatorDisplay;
    }

    public JLabel getDisplay() {
        manipulatorDisplay.setVisible(true);
        isDisplayed = true;
        return manipulatorDisplay;
    }

    public void hideDisplay() {
        manipulatorDisplay.setVisible(false);
        isDisplayed = false;
    }

    public BufferedImage catchFrame() {
        Mat out = new Mat();
        manipulatedImage.copyTo(out);
        return matToBufferedImage(out, null);
    }

    public Mat catchMat() {
        Mat out = new Mat();
        manipulatedImage.copyTo(out);
        return out;
    }

    public void setNext(MatFrameListener listener) {
        nextManipulator = listener;
    }

    public void onFrameReceived(Mat image) {
        manipulatedImage = run(image);
        if (manipulatedImage != null) {
            if (isDisplayed) {
                BufferedImage frame = catchFrame();
                if (frame != null && manipulatorDisplay.getGraphics() != null && manipulatorDisplay.isVisible())
                    manipulatorDisplay.getGraphics().drawImage(frame, 0, 0, null);
            }
            if (nextManipulator != null)
//                new Thread(() -> nextManipulator.onFrameReceived(manipulatedImage)).run();
                nextManipulator.onFrameReceived(manipulatedImage);
        }
    }

    protected abstract Mat run(Mat input);
}
