package vision.gui;

import javax.swing.*;
import java.awt.*;

/**
 * UI class to add named sliders
 */
public class TitledComponent extends JPanel {
    public TitledComponent(String sliderTitle, Component component) {
        super();
        JLabel title = new JLabel();
        title.setText(sliderTitle);
        this.add(title);
        this.add(component);
    }
}
