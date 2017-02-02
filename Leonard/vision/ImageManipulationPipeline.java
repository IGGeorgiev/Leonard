package vision;

import vision.capture.FrameReceivedListener;
import vision.capture.VideoCapture;
import vision.detection.NormalizeImage;
import vision.detection.UndistortImage;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

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

    VideoCapture   videoCapture   = new VideoCapture();
    UndistortImage undistortImage = new UndistortImage();
    NormalizeImage normalizeImage = new NormalizeImage();

    /**
     * This list is what determines what order (and which manipulations are applied to the input
     * video.
     */
    private LinkedList<ImageManipulator> pipeline = new LinkedList<ImageManipulator>() {{
        add(videoCapture);
        add(undistortImage);
        add(normalizeImage);
    }};

    @Override
    public void onFrameReceived(BufferedImage image) {

    }
}
