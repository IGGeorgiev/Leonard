package vision.gui;

import vision.capture.VideoCapture;
import vision.classification.SURFClassifier;
import vision.detection.ImageManipulationPipeline;
import vision.detection.ImageManipulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Ivan Georgiev (s1410984) on 19/02/17.
 * A Graphical Frontend for the classifier code
 */
public class ClassifierGUI extends JPanel {

    private ImageManipulationPipeline controller = ImageManipulationPipeline.getInstance();
    private LinkedList<ImageManipulator> pipeline = controller.pipeline;

    // Note - entry point to the pipeline is always the video feed
    public VideoCapture videoFeed = controller.videoCapture;

    public ClassifierGUI() {
        super(new GridLayout(2,2));
        chooseDisplays();
    }


    private void chooseDisplays() {
        ArrayList<Component> displayQueue = new ArrayList<>();

        // Choose what to display here
        for (ImageManipulator i : pipeline) {
            if (i instanceof SURFClassifier)
                displayQueue.add(i.getDisplay());
        }

        // Create View
        for (Component c : displayQueue)
            this.add(c);
    }

}
