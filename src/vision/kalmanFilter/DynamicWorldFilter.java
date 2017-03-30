package vision.kalmanFilter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.video.KalmanFilter;
import strategy.points.DynamicPoint;
import vision.Ball;
import vision.DynamicWorld;
import vision.RobotType;
import vision.robotAnalysis.DynamicWorldListener;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 28/02/17.
 */
public class DynamicWorldFilter implements DynamicWorldListener {

    private List<DynamicWorldListener> listeners = new ArrayList<>();
    private KalmanFilter ballFilter = new KalmanFilter(6,4,0, CvType.CV_32F);

    private DynamicPoint ballPoint;
    private int BALL_BOX = 20;

    public DynamicWorldFilter() {
        ballFilter.set_transitionMatrix(Mat.eye(6,6,CvType.CV_32F));
        ballFilter.set_measurementMatrix(Mat.eye(6,4, CvType.CV_32F));
        ballFilter.set_processNoiseCov(Mat.eye(6,6, CvType.CV_32F));
    }

    @Override
    public void nextDynamicWorld(DynamicWorld state) {
        Ball ball = state.getBall();
        if (ball != null) {
            VectorGeometry ballCenter = ball.location;
//            ballFilter.set

//            ball.location =

        } else {
            if (ballPoint == null) {
                informListeners(state);
                return;
            }
            ball = new Ball();

//            ballFilter.predict();
        }

    }

    private void informListeners(DynamicWorld state) {
        for (DynamicWorldListener listener : listeners)
            listener.nextDynamicWorld(state);
    }

    public void addFilterListener(DynamicWorldListener listener) {
        this.listeners.add(listener);
    }
}