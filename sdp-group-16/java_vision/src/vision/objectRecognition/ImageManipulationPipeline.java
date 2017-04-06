package vision.objectRecognition;

import org.opencv.core.Mat;
import vision.objectRecognition.detection.ImageManipulator;
import vision.objectRecognition.detection.manipulators.*;
import vision.rawInput.MatFrameListener;
import vision.rawInput.RawInputListener;
import vision.spotAnalysis.SpotAnalysisBase;
import vision.spotAnalysis.recursiveSpotAnalysis.RecursiveSpotAnalysis;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static vision.Vision.recursiveSpotAnalysis;
import static vision.gui.Preview.preview;
import static vision.objectRecognition.utils.Converter.imageToMat;
import static vision.objectRecognition.utils.Converter.matToImage;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A class to orchestrate
 */
public class ImageManipulationPipeline implements MatFrameListener, RawInputListener {

    private static final ImageManipulationPipeline instance = new ImageManipulationPipeline();

    public synchronized static ImageManipulationPipeline getInstance() { return instance; }


    private ImageManipulationPipeline() {
        // Initiate Pipeline
        for (int i = 0; i < pipeline.size() - 1; i++) {
            pipeline.get(i).setNext(pipeline.get(i + 1));
        }
        pipeline.getLast().setNext(this);
    }

//    VideoFileCapture                       videoFileCap   = new VideoFileCapture("vision/calibration/pre_saved_values/capture.mkv");
    public UndistortImage                  undistortImage = new UndistortImage();
    public HSVConverter                    hsvImage       = new HSVConverter();
//    private BackgroundSubtractionThreshold threshold      = new BackgroundSubtractionThreshold();
    private BackgroundSubtractionMOG       bgSubMOG       = new BackgroundSubtractionMOG();
    public GaussianBlurImage               gaussianBlur   = new GaussianBlurImage();
    public DilateImage                     dilateImage    = new DilateImage();
    private ErodeImage                     erodeImage     = new ErodeImage();
    private ApplyBinaryMask                applyBinaryMask= new ApplyBinaryMask(undistortImage);

    // Failures

    // Performance heavy
//    private RemoveSmallBlobs               rmSmallBlobs   = new RemoveSmallBlobs();
//    public NonLocalMeansDenoising          denoising      = new NonLocalMeansDenoising();

    // Multiple blurs deemed unnecessary
//    private GaussianBlurImage              gaussianBlur2  = new GaussianBlurImage();

    // Normalized RGB makes a noisy subtraction, which is difficult to threshold
//    NormalizeImage                       normalizeImage = new NormalizeImage()
// ;
    /**
     * This list is what determines what order (and which manipulations are applied to the input
     * video.
     */
    public LinkedList<ImageManipulator> pipeline = new LinkedList<ImageManipulator>() {{
        add(undistortImage);
        add(gaussianBlur);
        add(hsvImage);
//        add(threshold);
        add(bgSubMOG);
        add(erodeImage);
        add(dilateImage);
        add(applyBinaryMask);
    }};

    @Override
    public void onFrameReceived(Mat image, long time) {
        BufferedImage out = matToImage(image);
        preview.nextFrame(out, time);
        recursiveSpotAnalysis.nextFrame(out, time);
    }

    @Override
    public void nextFrame(BufferedImage image, long time) {
        pipeline.getFirst().onFrameReceived(imageToMat(image), time);
    }
}
