package strategy.navigation.potentialFieldNavigation;

import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Simon Rovder
 */
public class PotentialPreview extends JFrame {

    private JLabel label;
    private BufferedImage image;

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static double FACTOR = 10;
    private static double ZOOM = 0.58;

    public static final PotentialPreview preview = new PotentialPreview();

    private PotentialPreview(){
        super("Potentials");
        this.setLayout(null);
        this.setSize(WIDTH,HEIGHT + 50);

        this.label = new JLabel();
        this.label.setBounds(0,20,WIDTH,HEIGHT);
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

        this.getContentPane().add(this.label);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.setVisible(true);
    }

    public void updateField(PotentialField field){
        VectorGeometry point = new VectorGeometry();
        double potential;
        for(int y = HEIGHT/2 - 1; y > -HEIGHT/2 + 1; y--){
            for(int x = -WIDTH/2 + 1; x < WIDTH/2 - 1; x++){
                point.x = x*ZOOM;
                point.y = -y*ZOOM;
                potential = field.getPotentialAtPoint(point);
                int red = rangedRGB(potential);
                int green = rangedRGB(-potential);
                image.setRGB(x + WIDTH/2, y + HEIGHT/2, (new Color(red, green, 0)).getRGB());
            }
        }
        this.label.getGraphics().drawImage(image, 0, 0, null);
    }

    public static double normalize(double d){
        return 2/(1+Math.pow(Math.E, -d)) - 1;
    }

    private static int rangedRGB(double x){
        if(x < 0) return 0;
        int res = (int) (255 * normalize(x * FACTOR));
        if(res > 255) return 255;
        if(res < 50) return 50;
        return res;
    }
}
