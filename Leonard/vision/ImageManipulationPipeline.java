package vision;

import vision.capture.FrameReceivedListener;
import vision.capture.VideoCapture;
import vision.detection.*;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A class to orchestrate
 */
public class ImageManipulationPipeline implements FrameReceivedListener {

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
        add(dilateImage);
        add(erodeImage);
    }};

    @Override
    public void onFrameReceived(BufferedImage image) {
        // TODO Analyse processed image
    }
}
