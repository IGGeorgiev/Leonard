package vision.gui;

import vision.ImageManipulationPipeline;
import vision.classification.PatternMatcher;
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

    private ArrayList<ImageManipulator> displayedManipulators = new ArrayList<>();

    public ClassifierGUI() {
        super(new GridLayout(2,2));
        chooseDisplays();
    }


    private void chooseDisplays() {
        ArrayList<Component> displayQueue = new ArrayList<>();

        // Choose what to display here
        for (ImageManipulator i : pipeline) {
            if (i instanceof PatternMatcher) {
                displayQueue.add(i.getDisplay());
                displayedManipulators.add(i);
            }
        }

        // Create View
        displayQueue.forEach(this::add);
    }

    public void hideAll() {
        displayedManipulators.forEach(ImageManipulator::hideDisplay);
    }

    public void showAll() {displayedManipulators.forEach(ImageManipulator::getDisplay); }

}
