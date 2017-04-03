package vision.kalmanFilter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.video.KalmanFilter;
import vision.Ball;
import vision.DynamicWorld;
import vision.tools.VectorGeometry;

import static vision.constants.Constants.PITCH_HEIGHT;
import static vision.constants.Constants.PITCH_WIDTH;
import static vision.kalmanFilter.DynamicWorldFilter.enforceMinMax;

/**
 * Created by Ivan Georgiev (s1410984) on 03/04/17.
 */
public class BallFilter {
    private KalmanFilter ballFilter = new KalmanFilter(4,2,4, CvType.CV_32F);

    private VectorGeometry ballPoint = new VectorGeometry(0,0);
    private VectorGeometry ballVelocity = new VectorGeometry(0,0);
    private VectorGeometry ballAcceleration = new VectorGeometry(0,0);

    // Initialize Kalman Filter
    BallFilter() {
        double[] ball_transformation_data = {
                1, 0, 0.1, 0,
                0, 1, 0, 0.1,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        Mat ball_transformation_matrix = new Mat(4,4, CvType.CV_32F);
        ball_transformation_matrix.put(0,0,ball_transformation_data);
        ballFilter.set_transitionMatrix(ball_transformation_matrix);


        Mat ball_measurement_matrix = new Mat(4,4, CvType.CV_32F);
        double[] measureCov = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        ball_measurement_matrix.put(0,0, measureCov);
        ballFilter.set_measurementNoiseCov(ball_measurement_matrix);
        ballFilter.set_measurementMatrix(Mat.eye(4,4, CvType.CV_32F));

        double[] ball_processNoise_data = {
                0.01, 0, 0, 0,
                0, 0.01, 0, 0,
                0, 0, 0.01, 0,
                0, 0, 0, 0.01
        };
        Mat ball_processNoise_matrix = new Mat(4,4, CvType.CV_32F);
        ball_processNoise_matrix.put(0,0,ball_processNoise_data);
        ballFilter.set_processNoiseCov(Mat.eye(4,4, CvType.CV_32F));

        double[] ball_init_state_data = {
                0,
                0,
                0,
                0
        };
        Mat ball_init_state = new Mat(4,1, CvType.CV_32F);
        ball_init_state.put(0,0,ball_init_state_data);
        ballFilter.set_statePre(ball_init_state);
    }

    // Pass new world through filter
    void perform(DynamicWorld state) {
        Ball ball = state.getBall();
        if (ball != null) {
            VectorGeometry ballCenter = ball.location.clone();
            VectorGeometry ballVelocity = ball.velocity.clone();

            // Set location to predicted location
            Ball newBall = new Ball();

            newBall.location = ballPoint.clone();
            newBall.velocity = this.ballVelocity.clone();
            state.setBall(newBall);

            double[] controlMatData = {
                    ballAcceleration.x/2,
                    ballAcceleration.y/2,
                    ballAcceleration.x,
                    ballAcceleration.y
            };
            Mat controlMat = new Mat(4,1,CvType.CV_32F);
            controlMat.put(0,0,controlMatData);

            Mat prediction = ballFilter.predict(controlMat);
            double x = prediction.get(0,0)[0];
            double y = prediction.get(1,0)[0];
            double Vx = prediction.get(2,0)[0];
            double Vy = prediction.get(3,0)[0];

            // Update
            double[] ball_data = {
                    ballCenter.x,
                    ballCenter.y,
                    ballVelocity.x,
                    ballVelocity.y
            };
            Mat newState = new Mat(4,1, CvType.CV_32F);
            newState.put(0,0, ball_data);
            ballFilter.correct(newState);

            ballPoint.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            ballPoint.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            this.ballAcceleration = ballVelocity.clone().minus(this.ballVelocity);
            this.ballVelocity.x = Vx;
            this.ballVelocity.y = Vy;

        } else {
            // Set location to predicted location
            Ball newBall = new Ball();

            newBall.location = ballPoint.clone();
            newBall.velocity = ballVelocity.clone();
            state.setBall(newBall);

            double[] controlMatData = {
                    ballAcceleration.x/2,
                    ballAcceleration.y/2,
                    ballAcceleration.x,
                    ballAcceleration.y
            };
            Mat controlMat = new Mat(4,1,CvType.CV_32F);

            controlMat.put(0,0,controlMatData);

            Mat prediction = ballFilter.predict(controlMat);

            double x = prediction.get(0,0)[0];
            double y = prediction.get(1,0)[0];
            double Vx = prediction.get(2,0)[0];
            double Vy = prediction.get(3,0)[0];
            VectorGeometry ballVelocity = new VectorGeometry(Vx,Vy);
            ballPoint.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            ballPoint.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            this.ballAcceleration = ballVelocity.minus(this.ballVelocity);
            this.ballVelocity = ballVelocity.clone();
        }
    }
}
