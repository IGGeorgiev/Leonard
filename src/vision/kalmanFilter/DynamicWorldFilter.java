package vision.kalmanFilter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.video.KalmanFilter;
import strategy.points.DynamicPoint;
import vision.*;
import vision.robotAnalysis.DynamicWorldListener;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
import java.util.List;

import static vision.constants.Constants.PITCH_HEIGHT;
import static vision.constants.Constants.PITCH_WIDTH;

/**
 * Created by Ivan Georgiev (s1410984) on 28/02/17.
 */
public class DynamicWorldFilter implements DynamicWorldListener {

    private List<DynamicWorldListener> listeners = new ArrayList<>();
    private KalmanFilter ballFilter = new KalmanFilter(4,2,4, CvType.CV_32F);
    private KalmanFilter usFilter = new KalmanFilter(6,3,6, CvType.CV_32F);

    private VectorGeometry ballPoint = new VectorGeometry(0,0);
    private VectorGeometry ballVelocity = new VectorGeometry(0,0);
    private VectorGeometry ballAccellration = new VectorGeometry(0,0);

    private DirectedPoint r1Point = new DirectedPoint(-120, -80, 0);
    private DirectedPoint r1Velocity = new DirectedPoint(0, 0, 0);
    private DirectedPoint r1Acc = new DirectedPoint(0,0,0);

    public DynamicWorldFilter() {
        initialize_ball_filter();
        initialize_robotFilter(usFilter);
    }

    private void initialize_ball_filter() {
        double[] ball_transformation_data = {
                1, 0, 1, 0,
                0, 1, 0, 1,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        Mat ball_transformation_matrix = new Mat(4,4, CvType.CV_32F);
        ball_transformation_matrix.put(0,0,ball_transformation_data);
        ballFilter.set_transitionMatrix(ball_transformation_matrix);


        Mat ball_measurement_matrix = new Mat(4,4, CvType.CV_32F);
        double[] measureCov = {
                10, 0, 0, 0,
                0, 10, 0, 0,
                0, 0, 10, 0,
                0, 0, 0, 10
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

    private void initialize_robotFilter(KalmanFilter filter) {
        double[] transformation_data = {
                1, 0, 0, 1, 0, 0,
                0, 1, 0, 0, 1, 0,
                0, 0, 1, 0, 0, 1,
                0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 1
        };
        Mat transformation_matrix = new Mat(6,6, CvType.CV_32F);
        transformation_matrix.put(0,0,transformation_data);
        filter.set_transitionMatrix(transformation_matrix);


        double[] measureCov = {
                10, 0, 0, 0, 0, 0,
                0, 10, 0, 0, 0, 0,
                0, 0, 10, 0, 0, 0,
                0, 0, 0, 10, 0, 0,
                0, 0, 0, 0, 10, 0,
                0, 0, 0 ,0 , 0, 10
        };
        Mat measurement_matrix = new Mat(6,6, CvType.CV_32F);
        measurement_matrix.put(0,0, measureCov);
        filter.set_measurementNoiseCov(measurement_matrix);
        filter.set_measurementMatrix(Mat.eye(6,6, CvType.CV_32F));

        double[] processNoise_data = {
                0.01, 0, 0, 0, 0, 0,
                0, 0.01, 0, 0, 0, 0,
                0, 0, 0.01, 0, 0, 0,
                0, 0, 0, 0.01, 0, 0,
                0, 0, 0, 0, 0.01, 0,
                0, 0, 0, 0, 0, 0.01
        };
        Mat processNoise_matrix = new Mat(6,6, CvType.CV_32F);
        processNoise_matrix.put(0,0,processNoise_data);
        filter.set_processNoiseCov(Mat.eye(6,6, CvType.CV_32F));

        double[] init_state_data = {
                -120,
                -80,
                0,
                0,
                0,
                0
        };

        Mat ball_init_state = new Mat(6,1, CvType.CV_32F);
        ball_init_state.put(0,0,init_state_data);
        filter.set_statePre(ball_init_state);
    };

    @Override
    public void nextDynamicWorld(DynamicWorld state) {
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
                    ballAccellration.x/2,
                    ballAccellration.y/2,
                    ballAccellration.x,
                   ballAccellration.y
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
            this.ballAccellration = ballVelocity.clone().minus(this.ballVelocity);
            this.ballVelocity.x = Vx;
            this.ballVelocity.y = Vy;

        } else {
            // Set location to predicted location
            Ball newBall = new Ball();

            newBall.location = ballPoint.clone();
            newBall.velocity = ballVelocity.clone();
            state.setBall(newBall);

            double[] controlMatData = {
                    ballAccellration.x/2,
                    ballAccellration.y/2,
                    ballAccellration.x,
                    ballAccellration.y
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
            this.ballAccellration = ballVelocity.minus(this.ballVelocity);
            this.ballVelocity = ballVelocity.clone();
        }

        // Robot Filter
        Robot r = state.getRobot(RobotType.FRIEND_2);
        if (r != null && r.velocity != null) {
            DirectedPoint rCenter = r.location.clone();
            DirectedPoint r1Velocity = r.velocity.clone();

            // Set location to predicted location
            Robot newRobot = new Robot();

            newRobot.location = r1Point.clone();
            newRobot.velocity = this.r1Velocity.clone();
            newRobot.type = RobotType.FRIEND_2;
            newRobot.alias = r.alias;

            state.setRobot(newRobot);

            double[] controlMatData = {
                    r1Acc.x/2,
                    r1Acc.y/2,
                    r1Acc.direction/2,
                    r1Acc.x,
                    r1Acc.y,
                    r1Acc.direction
            };
            Mat controlMat = new Mat(6,1,CvType.CV_32FC1);
            controlMat.put(0,0,controlMatData);

            Mat prediction = usFilter.predict(controlMat);
            double x = prediction.get(0,0)[0];
            double y = prediction.get(1,0)[0];
            double h = prediction.get(2,0)[0];
            double Vx = prediction.get(3,0)[0];
            double Vy = prediction.get(4,0)[0];
            double Vh = prediction.get(5,0)[0];

            // Update
            double[] r1_data = {
                    rCenter.x,
                    rCenter.y,
                    rCenter.direction,
                    r1Velocity.x,
                    r1Velocity.y,
                    r1Velocity.direction
            };
            Mat newState = new Mat(6,1, CvType.CV_32F);
            newState.put(0,0, r1_data);
            usFilter.correct(newState);

            r1Point.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            r1Point.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            r1Point.direction = h;
            this.r1Acc = new DirectedPoint(r1Velocity.x - this.r1Velocity.x,
                    r1Velocity.y - this.r1Velocity.y,
                    r1Velocity.direction - this.r1Velocity.direction);
            this.r1Velocity.x = Vx;
            this.r1Velocity.y = Vy;
            this.r1Velocity.direction = Vh;

        } else {
            // Set location to predicted location
            Robot newRobot = new Robot();

            newRobot.location = r1Point.clone();
            newRobot.velocity = this.r1Velocity.clone();
            newRobot.type = RobotType.FRIEND_2;
            newRobot.alias = RobotAlias.LEONARD;
            state.setRobot(newRobot);

            double[] controlMatData = {
                    r1Acc.x/2,
                    r1Acc.y/2,
                    r1Acc.direction/2,
                    r1Acc.x,
                    r1Acc.y,
                    r1Acc.direction
            };
            Mat controlMat = new Mat(6,1,CvType.CV_32FC1);
            controlMat.put(0,0,controlMatData);

            Mat prediction = usFilter.predict(controlMat);
            double x = prediction.get(0,0)[0];
            double y = prediction.get(1,0)[0];
            double h = prediction.get(2,0)[0];
            double Vx = prediction.get(3,0)[0];
            double Vy = prediction.get(4,0)[0];
            double Vh = prediction.get(5,0)[0];

            DirectedPoint r1Velocity = new DirectedPoint(Vx, Vy, Vh);

            r1Point.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            r1Point.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            r1Point.direction = h;
            this.r1Acc = new DirectedPoint(r1Velocity.x - this.r1Velocity.x,
                    r1Velocity.y - this.r1Velocity.y,
                    r1Velocity.direction - this.r1Velocity.direction);
            this.r1Velocity.x = Vx;
            this.r1Velocity.y = Vy;
            this.r1Velocity.direction = Vh;
        }

        informListeners(state);
    }

    private void informListeners(DynamicWorld state) {
        for (DynamicWorldListener listener : listeners)
            listener.nextDynamicWorld(state);
    }

    public double enforceMinMax(double max, double min, double value) {
        return (max < value) ? max : (min > value) ? min : value;
    }

    public void addFilterListener(DynamicWorldListener listener) {
        this.listeners.add(listener);
    }
}