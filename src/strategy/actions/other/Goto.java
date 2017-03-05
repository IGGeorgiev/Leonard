package strategy.actions.other;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.Strategy;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Goto extends ActionBase {
    private double rotation;
    public Goto(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " GOTO";
    }

    @Override
    public void enterState(int newState) {
        if (newState == 1) {
            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 20));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        } else if (newState == 3) {
            System.out.println("trying to move forward!!!");
            this.robot.MOTION_CONTROLLER.clearObstacles();
            // TODO: should move forward here
//            this.robot.ACTION_CONTROLLER.setAction(new Demo(robot, 150, 255, 255, 150));
        } else if (newState == 4) {
            System.out.println("yo fix rotation!");
            double constant = 300*rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(constant,constant,constant,constant);
        } else { // state 2
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) { // go to point
            this.enterState(1);
        } else if (this.state == 2) { // halt
            throw new ActionException(true, false);
        } else if (this.state == 3) { // move forward
            this.enterState(2);
        } else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 5) {
            System.out.println("I am too close! ");
            this.enterState(2);
        } else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 40) {
            System.out.println("================ hitted tolerance!! ===========");
            double angle = VectorGeometry.angle(0,1,-1,1); // 45 degrees
            VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
            VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
            double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
            if (Math.abs(rotation) >= 0.1) {
                this.rotation = rotation;
                this.enterState(4);
                return;
            } else {
                this.enterState(3);
            }
        }
    }
}




