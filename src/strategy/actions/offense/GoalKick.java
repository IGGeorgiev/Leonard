package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

public class GoalKick extends ActionBase {

    private double rotation;

    public GoalKick(RobotBase robot) {
        super(robot, new EnemyGoal());
        this.rawDescription = "GoalKick";
    }

    @Override
    public void enterState(int newState) {

        if (newState == 1) {
            System.out.println("Setting heading and activating grabber");
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(5);
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(true);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
        } else if (newState == 2) {
            System.out.println("Rotating towards the goal.");
            this.state = 0;
            double constant = 300 * rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
        } else if (newState == 3) {
            ((Fred)this.robot).GRABBER_CONTROLLER.grab(1, 2000);
            ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(255,-255,-255,255);
            System.out.println("Moving forward now and kicking now. ");
            ((Fred)this.robot).KICKER_CONTROLLER.setActive(true);
        } else { // state 4
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
        }
    }

    @Override
    public void tok() throws ActionException {

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);

        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) {
            this.enterState(1);
        } else if (this.state == 2) { // rotate towards the goal.
            this.enterState(3);
        } else if (this.state == 3) {// kick the ball
        } else if (this.state == 4) {// stop GoalKick
            throw new ActionException(true, false);
        }

        //The variable point is set in the super(robot, new EnemyGoal()) line in the class initializer.
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) > 5) {
            //Not sure how to make so that when this is called we start GoToBall again... maybe in Behave???
            System.out.println("We are too close far away from the ball, stopping GoalKick");
            this.enterState(4);
        }else {

            if (Math.abs(rotation) >= 0.1) {
                this.rotation = rotation;
                this.enterState(1);
                return;
            } else {
                this.enterState(2);
            }
        }
    }
}
