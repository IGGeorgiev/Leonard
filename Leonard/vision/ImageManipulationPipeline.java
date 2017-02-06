package vision;

import org.opencv.core.Mat;
import vision.capture.MatFrameListener;
import vision.capture.VideoCapture;
import vision.detection.*;

import java.util.LinkedList;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A class to orchestrate
 */
public class ImageManipulationPipeline implements MatFrameListener {

    private static final ImageManipulationPipeline instance = new ImageManipulationPipeline();

    static ImageManipulationPipeline getInstance() { return instance; }


    private ImageManipulationPipeline() {
        // Initiate Pipeline
        for (int i = 0; i < pipeline.size() - 1; i++) {
            pipeline.get(i).setNext(pipeline.get(i + 1));
        }
        pipeline.getLast().setNext(this);
    }

    VideoCapture                           videoCapture   = new VideoCapture();
    private UndistortImage                 undistortImage = new UndistortImage();
    NormalizeImage                         normalizeImage = new NormalizeImage();
    private BackgroundSubtractionThreshold threshold      = new BackgroundSubtractionThreshold();
    private GaussianBlurImage              gaussianBlur   = new GaussianBlurImage();
    private DilateImage                    dilateImage    = new DilateImage();
    private ErodeImage                     erodeImage     = new ErodeImage();
    private ApplyBinaryMask                applyBinaryMask= new ApplyBinaryMask(undistortImage);

    // Failures

    // Performance heavy
//    private RemoveSmallBlobs               rmSmallBlobs   = new RemoveSmallBlobs();

    // Multiple blurs deemed unnecessary
//    private GaussianBlurImage              gaussianBlur2  = new GaussianBlurImage();

    /**
     * This list is what determines what order (and which manipulations are applied to the input
     * video.
     */
    LinkedList<ImageManipulator> pipeline = new LinkedList<ImageManipulator>() {{
        add(videoCapture);
        add(undistortImage);
        add(normalizeImage);
        add(threshold);
        add(gaussianBlur);
        add(erodeImage);
        add(dilateImage);
        add(applyBinaryMask);
    }};

    @Override
    public void onFrameReceived(Mat image) {
        // TODO Analyse processed image
    }
}
