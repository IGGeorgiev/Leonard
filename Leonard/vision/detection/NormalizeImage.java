package vision.detection;

import org.opencv.core.Mat;
import vision.ImageManipulator;

import java.awt.image.BufferedImage;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToImage;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A converter to normalized RGB
 */
public class NormalizeImage extends ImageManipulator {
    
    @Override
    protected BufferedImage run(BufferedImage input) {

        Mat mat = imageToMat(input);

        int width = mat.width();
        int height = mat.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[] rgb = mat.get(y, x);
                double r = rgb[0];
                double g = rgb[1];
                double b = rgb[2];
                rgb[0] = 255 * r / (r + g + b);
                rgb[1] = 255 * g / (r + g + b);
                rgb[2] = 255 * b / (r + g + b);
                mat.put(y, x, rgb);
            }
        }

        return matToImage(mat);
    }
}
