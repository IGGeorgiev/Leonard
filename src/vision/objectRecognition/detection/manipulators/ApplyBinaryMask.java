package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import vision.objectRecognition.detection.ImageManipulator;

/**
 * Created by Ivan Georgiev (s1410984) on 06/02/17.
 * Apply mask onto the original feed
 */
public class ApplyBinaryMask extends ImageManipulator {

    private ImageManipulator originalImageHolder;

    public ApplyBinaryMask(ImageManipulator originalImage) {
        this.originalImageHolder = originalImage;
    }

    @Override
    protected Mat run(Mat input) {
        Mat original = originalImageHolder.catchMat();
        Mat output = new Mat();
        original.copyTo(output, input);
        return output;
    }
}
