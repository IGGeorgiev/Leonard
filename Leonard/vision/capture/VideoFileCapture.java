package vision.capture;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import vision.detection.ImageManipulator;

/**
 * Created by Ivan on 13/02/2017.
 * Class to capture video
 */
public class VideoFileCapture extends ImageManipulator {

    public VideoFileCapture(String filePath) {

        VideoCapture cap = new VideoCapture(filePath);

        if (!cap.isOpened()) {
            System.out.println("ERROR Opening video file.");
        }

        while(true) {
            Mat frame = new Mat();

            boolean success = cap.read(frame);

            if (!success) {
                System.out.println("ERROR reading video file.");
                break;
            }

            onFrameReceived(frame);
        }

    }

    @Override
    protected Mat run(Mat input) {
        return input;
    }
}
