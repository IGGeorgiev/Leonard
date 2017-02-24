package vision.objectRecognition.detection;

import java.awt.*;
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 02/02/17.
 * Class extending ImageManipulator to add GUI elements to change Manipulator's behaviour
 */
public abstract class ImageManipulatorWithOptions extends ImageManipulator {
    public abstract Component getModificationGUI();
    public abstract void saveModificationSettings(Properties prop);
    public abstract void loadModificationSettings(Properties prop);
}
