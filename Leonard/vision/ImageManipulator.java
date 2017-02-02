package vision;

import vision.capture.FrameReceivedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * An abstract class to manage the pipeline of manipulators the input video goes through
 */
public abstract class ImageManipulator implements FrameReceivedListener {

    private FrameReceivedListener nextManipulator = null;
    private JLabel manipulatorDisplay;
    private boolean isDisplayed = false;
    private BufferedImage manipulatedImage = null;

    public JLabel getDisplay(boolean setIsDisplayed) {
        if (manipulatorDisplay == null)
            manipulatorDisplay = new JLabel();
        isDisplayed = setIsDisplayed;
        return manipulatorDisplay;
    }

    public JLabel getDisplay() {
        if (manipulatorDisplay == null)
            manipulatorDisplay = new JLabel();
        isDisplayed = true;
        return manipulatorDisplay;
    }

    public void hideDisplay() {
        isDisplayed = false;
    }

    public BufferedImage catchFrame() {
        return manipulatedImage;
    }

    void setNext(FrameReceivedListener listener) {
        nextManipulator = listener;
    }

    @Override
    public void onFrameReceived(BufferedImage image) {
        manipulatedImage = run(image);
        if (isDisplayed)
            manipulatorDisplay.getGraphics().drawImage(manipulatedImage, 0, 0, null);
        if (nextManipulator != null)
            new Thread(() -> nextManipulator.onFrameReceived(manipulatedImage));
    }

    protected abstract BufferedImage run(BufferedImage input);
}
