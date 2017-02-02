package vision;

import vision.capture.FrameReceivedListener;
import vision.capture.VideoCapture;
import vision.detection.BackgroundSubtractionThreshold;
import vision.detection.GaussianBlurImage;
import vision.detection.NormalizeImage;
import vision.detection.UndistortImage;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A class to orchestrate
 */
public class ImageManipulationPipeline implements FrameReceivedListener {

    private static ImageManipulationPipeline instance = new ImageManipulationPipeline();

    static ImageManipulationPipeline getInstance() { return instance; }


    private ImageManipulationPipeline() {
        // Initiate Pipeline
        for (int i = 0; i < pipeline.size() - 1; i++) {
            pipeline.get(i).setNext(pipeline.get(i + 1));
        }
        pipeline.getLast().setNext(this);
    }

    private VideoCapture                   videoCapture   = new VideoCapture();
    private UndistortImage                 undistortImage = new UndistortImage();
    private NormalizeImage                 normalizeImage = new NormalizeImage();
    private BackgroundSubtractionThreshold threshold      = new BackgroundSubtractionThreshold();
    private GaussianBlurImage              gaussianBlur   = new GaussianBlurImage();

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
    }};

    @Override
    public void onFrameReceived(BufferedImage image) {
        // TODO Analyse processed image
    }
}
