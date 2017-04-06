package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import vision.objectRecognition.detection.ImageManipulator;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A converter to normalized RGB
 */
public class NormalizeImage extends ImageManipulator {
    
    @Override
    protected Mat run(Mat mat) {

        int width = mat.width();
        int height = mat.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[] rgb = mat.get(y, x);
                double b = rgb[0];
                double g = rgb[1];
                double r = rgb[2];
                rgb[0] = 255 * b / (r + g + b);
                rgb[1] = 255 * g / (r + g + b);
                rgb[2] = 255 * r / (r + g + b);
                mat.put(y, x, rgb);
            }
        }

        return mat;
    }
}
