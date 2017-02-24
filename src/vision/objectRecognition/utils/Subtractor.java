package vision.objectRecognition.utils;

import org.opencv.core.Mat;

/**
 * Created by Ivan Georgiev (s1410984) on 30/01/17.
 * Matrix subtraction functions
 */
public class Subtractor {

    // Basic 3 channel background subtraction
    public static Mat subtract(Mat a, Mat b) {
        assert a.height() == b.height() && a.width() == b.width() && a.type() == b.type();
        Mat out = new Mat(a.rows(), a.cols(), a.type());
        for (int i = 0; i < a.width(); i++) {
            for (int j = 0; j < a.height(); j++) {
                double[] dataA = a.get(j,i);
                double[] dataB = b.get(j,i);
                double[] data = new double[3];
                data[0] = Math.abs(dataA[0] - dataB[0]);
                data[1] = Math.abs(dataA[1] - dataB[1]);
                data[2] = Math.abs(dataA[2] - dataB[2]);
                out.put(j,i, data);
            }
        }
        return out;
    }
}
