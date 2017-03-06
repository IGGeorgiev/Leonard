package vision.objectRecognition.detection;

import org.opencv.core.Mat;
import vision.rawInput.MatFrameListener;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static vision.objectRecognition.utils.Converter.matToBufferedImage;

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
        if (manipulatedImage.height() != 0 || manipulatedImage.width() != 0) {
            Mat out = new Mat();
            manipulatedImage.copyTo(out);
            return matToBufferedImage(out, null);
        } else {
            return new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
        }
    }

    public Mat catchMat() {
        Mat out = new Mat();
        manipulatedImage.copyTo(out);
        return out;
    }

    public void setNext(MatFrameListener listener) {
        nextManipulator = listener;
    }

    public void onFrameReceived(Mat image, long time) {
        manipulatedImage = run(image);
        if (manipulatedImage != null) {
            if (isDisplayed) {
                BufferedImage frame = catchFrame();
                if (frame != null && manipulatorDisplay.getGraphics() != null && manipulatorDisplay.isVisible())
                    manipulatorDisplay.getGraphics().drawImage(frame, 0, 0, null);
            }
            if (nextManipulator != null)
                new Thread(() -> nextManipulator.onFrameReceived(manipulatedImage, time)).start();
        }
    }

    protected abstract Mat run(Mat input);
}
