package vision.calibration;

import org.opencv.core.Mat;
import vision.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Created by s1410984 on 29/01/17.
 */
public class CalibrateEmptyPitchButton extends JButton implements ActionListener {

    private VideoCapture videoFeed;

    public CalibrateEmptyPitchButton(VideoCapture vc) {
        super();
        videoFeed = vc;
        this.setText("Calibrate Empty Field");
        this.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        createNormalizedImageAndSaveToPath(new File("./normalized_rgb.jpg"));
    }

    private void createNormalizedImageAndSaveToPath(File file) {
        BufferedImage img1 = videoFeed.getCurrentImage();
        Mat mat = new Mat();
        byte[] pixels = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        System.out.println("Iterating over " + channels + " channels");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[] rgb = mat.get(x, y);
                double r = rgb[0];
                double g = rgb[1];
                double b = rgb[2];
                rgb[0] = r / (r + g + b);
                rgb[1] = g / (r + g + b);
                rgb[2] = b / (r + g + b);
                mat.put(x, y, rgb);
            }
        }
        // Create an empty image in matching format
        BufferedImage img = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        try {
            boolean craeted = file.createNewFile();
            ImageIO.write(img, ".png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
