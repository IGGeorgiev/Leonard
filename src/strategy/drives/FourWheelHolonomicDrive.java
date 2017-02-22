package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 * Modified by Group 15, 2017 to suit the need where the grabber/kicker is not in the same direction
 * as the front wheel
 */

public class FourWheelHolonomicDrive implements DriveInterface{

    public int MAX_ROTATION = 30;
    public int MAX_MOTION = 200;

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor){
        assert(port instanceof FourWheelHolonomicRobotPort);

        //********** Rotation Modification **********//
//        rotation += 0.785;
        //********** Rotation Modification **********//

        VectorGeometry dir = new VectorGeometry();
        force.copyInto(dir).coordinateRotation(force.angle() - location.direction);
        factor = Math.min(1, factor);

        double lim = this.MAX_MOTION - Math.abs(rotation* this.MAX_ROTATION *factor);

        double front = dir.y;
        double left = -dir.x;
        double back = -dir.y;
        double right = dir.x;
        double normalizer = Math.max(Math.max(Math.abs(left), Math.abs(right)), Math.max(Math.abs(front), Math.abs(back)));

        normalizer = lim/normalizer*factor;
        front = front*normalizer + rotation * this.MAX_ROTATION;
        back  = back*normalizer + rotation * this.MAX_ROTATION;
        left  = left*normalizer + rotation * this.MAX_ROTATION;
        right = right*normalizer + rotation * this.MAX_ROTATION;

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

    }


}
