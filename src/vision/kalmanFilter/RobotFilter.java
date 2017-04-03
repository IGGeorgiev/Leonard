package vision.kalmanFilter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.video.KalmanFilter;
import vision.DynamicWorld;
import vision.Robot;
import vision.RobotAlias;
import vision.RobotType;
import vision.tools.DirectedPoint;

import static vision.constants.Constants.PITCH_HEIGHT;
import static vision.constants.Constants.PITCH_WIDTH;
import static vision.kalmanFilter.DynamicWorldFilter.enforceMinMax;

/**
 * Created by Ivan Georgiev (s1410984) on 03/04/17.
 */
public class RobotFilter {

    private KalmanFilter rFilter = new KalmanFilter(6,3,6, CvType.CV_32F);

    private DirectedPoint rPoint = new DirectedPoint(-120, -80, 0);
    private DirectedPoint rVelocity = new DirectedPoint(0, 0, 0);
    private DirectedPoint rAcc = new DirectedPoint(0,0,0);
    private RobotAlias rAlias = RobotAlias.UNKNOWN;
    private RobotType rType;

    RobotFilter(RobotType robotType, DirectedPoint startingPoint) {
        rType = robotType;
        rPoint = startingPoint;
        double[] transformation_data = {
                1, 0, 0, 0.1, 0, 0,
                0, 1, 0, 0, 0.1, 0,
                0, 0, 1, 0, 0, 0.01,
                0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 1
        };
        Mat transformation_matrix = new Mat(6,6, CvType.CV_32F);
        transformation_matrix.put(0,0,transformation_data);
        rFilter.set_transitionMatrix(transformation_matrix);


        double[] measureCov = {
                1, 0, 0, 0, 0, 0,
                0, 1, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0,
                0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 1, 0,
                0, 0, 0 ,0 , 0, 1
        };
        Mat measurement_matrix = new Mat(6,6, CvType.CV_32F);
        measurement_matrix.put(0,0, measureCov);
        rFilter.set_measurementNoiseCov(measurement_matrix);
        rFilter.set_measurementMatrix(Mat.eye(6,6, CvType.CV_32F));

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
        rFilter.set_processNoiseCov(Mat.eye(6,6, CvType.CV_32F));

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
        rFilter.set_statePre(ball_init_state);
    }

    void perform(DynamicWorld state) {
        // Robot Filter
        Robot r = state.getRobot(rType);
        if (r != null && r.velocity != null) {
            DirectedPoint rCenter = r.location.clone();
            DirectedPoint r1Velocity = r.velocity.clone();

            // Set location to predicted location
            Robot newRobot = new Robot();

            newRobot.location = rPoint.clone();
            newRobot.velocity = this.rVelocity.clone();
            newRobot.type = rType;
            newRobot.alias = r.alias;
            rAlias = r.alias;

            state.setRobot(newRobot);

            double[] controlMatData = {
                    rAcc.x/2,
                    rAcc.y/2,
                    rAcc.direction/2,
                    rAcc.x,
                    rAcc.y,
                    rAcc.direction
            };
            Mat controlMat = new Mat(6,1,CvType.CV_32FC1);
            controlMat.put(0,0,controlMatData);

            Mat prediction = rFilter.predict(controlMat);
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
            rFilter.correct(newState);

            rPoint.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            rPoint.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            rPoint.direction = h;
            this.rAcc = new DirectedPoint(r1Velocity.x - this.rVelocity.x,
                    r1Velocity.y - this.rVelocity.y,
                    r1Velocity.direction - this.rVelocity.direction);
            this.rVelocity.x = Vx;
            this.rVelocity.y = Vy;
            this.rVelocity.direction = Vh;

        } else {
            // Set location to predicted location
            Robot newRobot = new Robot();

            newRobot.location = rPoint.clone();
            newRobot.velocity = this.rVelocity.clone();
            newRobot.type = rType;
            newRobot.alias = rAlias;
            state.setRobot(newRobot);

            double[] controlMatData = {
                    rAcc.x/2,
                    rAcc.y/2,
                    rAcc.direction/2,
                    rAcc.x,
                    rAcc.y,
                    rAcc.direction
            };
            Mat controlMat = new Mat(6,1,CvType.CV_32FC1);
            controlMat.put(0,0,controlMatData);

            Mat prediction = rFilter.predict(controlMat);
            double x = prediction.get(0,0)[0];
            double y = prediction.get(1,0)[0];
            double h = prediction.get(2,0)[0];
            double Vx = prediction.get(3,0)[0];
            double Vy = prediction.get(4,0)[0];
            double Vh = prediction.get(5,0)[0];

            DirectedPoint r1Velocity = new DirectedPoint(Vx, Vy, Vh);

            rPoint.x = enforceMinMax(PITCH_WIDTH/2, -PITCH_WIDTH/2, x);
            rPoint.y = enforceMinMax(PITCH_HEIGHT/2, -PITCH_HEIGHT/2, y);
            rPoint.direction = h;
            this.rAcc = new DirectedPoint(r1Velocity.x - this.rVelocity.x,
                    r1Velocity.y - this.rVelocity.y,
                    r1Velocity.direction - this.rVelocity.direction);
            this.rVelocity.x = Vx;
            this.rVelocity.y = Vy;
            this.rVelocity.direction = Vh;
        }
    }
}
