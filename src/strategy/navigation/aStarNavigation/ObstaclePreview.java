package strategy.navigation.aStarNavigation;

import vision.constants.Constants;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Simon Rovder
 */
public class ObstaclePreview extends JFrame {

    private JLabel label;
    private BufferedImage image;

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static double FACTOR = 10;
    private static double ZOOM = 0.58;

    public static final ObstaclePreview preview = new ObstaclePreview();

    private ObstaclePreview(){
        super("Obstacles");
        this.setLayout(null);
        this.setSize(WIDTH,HEIGHT + 50);

        this.label = new JLabel();
        this.label.setBounds(0,20,WIDTH,HEIGHT);
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

        this.getContentPane().add(this.label);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.setVisible(true);
    }

    public void updateField(ObstacleField field, VectorGeometry destination){
        VectorGeometry point = new VectorGeometry();
        double potential;
        int halfHeight = Constants.PITCH_HEIGHT / 2;
        int halfWidth = Constants.PITCH_WIDTH / 2;
        for(int y = 0; y < Constants.PITCH_HEIGHT - 1; y++){
            for(int x = 0; x < Constants.PITCH_WIDTH - 1; x++){
                if(field.isFree(x - halfWidth, y - halfHeight, 500, 500)){
                    image.setRGB(x, Constants.PITCH_HEIGHT - y, (new Color(0, 255, 0)).getRGB());
                } else {
                    image.setRGB(x, Constants.PITCH_HEIGHT - y, (new Color(255,0,0)).getRGB());
                }

            }
        }

    }

    public void setWhite(int x, int y){
        int halfHeight = Constants.PITCH_HEIGHT / 2;
        int halfWidth = Constants.PITCH_WIDTH / 2;
        try{
            this.image.setRGB(x + halfWidth, y + halfHeight, Color.WHITE.getRGB());
        } catch(Exception e){
//            System.out.println("Bad destination of: " + x + " : " + y);
        }
    }

    public void flush(){
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
