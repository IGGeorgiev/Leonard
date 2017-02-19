package vision.detection;

import org.opencv.core.Mat;
import vision.capture.MatFrameListener;

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
        isDisplayed = true;
        return manipulatorDisplay;
    }

    public void hideDisplay() {
        isDisplayed = false;
    }

    public BufferedImage catchFrame() {
        return matToBufferedImage(manipulatedImage, null);
    }

    public Mat catchMat() {
        return manipulatedImage;
    }

    void setNext(MatFrameListener listener) {
        nextManipulator = listener;
    }

    public void onFrameReceived(Mat image) {
        manipulatedImage = run(image);
        if (manipulatedImage != null) {
            if (isDisplayed)
                manipulatorDisplay.getGraphics().drawImage(catchFrame(), 0, 0, null);
            if (nextManipulator != null)
                new Thread(() -> nextManipulator.onFrameReceived(manipulatedImage)).run();
        }
    }

    protected abstract Mat run(Mat input);
}
