package vision.objectRecognition.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by Ivan Georgiev (s1410984) on 30/01/17.
 * Class to convert to and from OpenCV's Mat
 */
public class Converter {

    public static BufferedImage matToImage(Mat mat) {


        // Create an empty image in matching format
        BufferedImage out;
        if (mat.type() == 0)
            return matToBinaryImage(mat);
        else
            out = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        return out;
    }

    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage(Mat matrix, BufferedImage bimg)
    {
        if ( matrix != null ) {
            int cols = matrix.cols();
            int rows = matrix.rows();
            int elemSize = (int)matrix.elemSize();
            byte[] data = new byte[cols * rows * elemSize];
            int type;
            matrix.get(0, 0, data);
            switch (matrix.channels()) {
                case 1:
                    type = BufferedImage.TYPE_BYTE_GRAY;
                    break;
                case 3:
                    type = BufferedImage.TYPE_3BYTE_BGR;
                    // bgr to rgb
                    byte b;
                    for(int i=0; i<data.length; i=i+3) {
                        b = data[i];
                        data[i] = data[i+2];
                        data[i+2] = b;
                    }
                    break;
                default:
                    return null;
            }

            // Reuse existing BufferedImage if possible
            if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
                bimg = new BufferedImage(cols, rows, type);
            }
            bimg.getRaster().setDataElements(0, 0, cols, rows, data);
        } else { // mat was null
            bimg = null;
        }
        return bimg;
    }

    public static Mat imageToMat(BufferedImage img) {
        Mat mat = new Mat(new Size(img.getWidth(), img.getHeight()), CvType.CV_8UC3);
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        return mat;
    }

//    public static Mat binaryImageToMat(BufferedImage img) {
//        Mat mat = new Mat(new Size(img.getWidth(), img.getHeight()), CvType.CV_8U);
//        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
//        mat.put(0, 0, pixels);
//        return mat;
//    }


    private static BufferedImage matToBinaryImage(Mat mat) {

        // Create an empty image in matching format
        BufferedImage out = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        return out;
    }
}
