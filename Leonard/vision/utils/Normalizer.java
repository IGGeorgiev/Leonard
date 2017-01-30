package vision.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToImage;

/**
 * Created by Ivan Georgiev (s1410984) on 29/01/17.
 * Function to Create a normalized RGB image from given image.
 */
public class Normalizer {

    public static BufferedImage normalizeRGB(BufferedImage img) {

        Mat mat = imageToMat(img);

        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
//        System.out.println("Iterating over a " + width + "x" + height + "x" + channels + " matrix");
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

        BufferedImage out = matToImage(mat);

        return out;
    }

}
