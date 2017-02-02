package vision.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

/**
 * Created by Ivan Georgiev (s1410984) on 30/01/17.
 * Class to convert to and from OpenCV's Mat
 */
public class Converter {

    public static BufferedImage matToImage(Mat mat) {
        // Create an empty image in matching format
        BufferedImage out = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        return out;
    }

    public static Mat imageToMat(BufferedImage img) {
        Mat mat = new Mat(new Size(img.getWidth(), img.getHeight()), CvType.CV_8UC3);
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        return mat;
    }

    public static Mat binaryImageToMat(BufferedImage img) {
        Mat mat = new Mat(new Size(img.getWidth(), img.getHeight()), CvType.CV_8U);
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        return mat;
    }


    public static BufferedImage matToBinaryImage(Mat mat) {

        // Create an empty image in matching format
        BufferedImage out = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        return out;
    }
}
