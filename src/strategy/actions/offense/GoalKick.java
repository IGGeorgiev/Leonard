package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.Timer;


public class GoalKick extends ActionBase {


    private Ball ball;
    private boolean fixed = false;
    private VectorGeometry emgoal;
    private double rotation;
    private Timer timer;

    public GoalKick(RobotBase robot) {
        super(robot, new EnemyGoal());
        this.rawDescription = "GoalKick";
    }

    @Override
    public void enterState(int newState) {

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        EnemyGoal enemyGoal = new EnemyGoal();
//        if (us == null || ball == null) return;

//        VectorGeometry ballVec = new VectorGeometry(ball.location.x, ball.location.y);
//        VectorGeometry emgoal = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY());
//        VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ballVec, 20);
//
//        //        this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle((int) ball.location.x, (int) ball.location.y, 10));
//        this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
//        this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) kickingPoint.x, (int) kickingPoint.y));
//        this.robot.MOTION_CONTROLLER.setTolerance(5);
//        ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
//        ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
//        this.state = 0;
        this.state = newState;

        if (newState == 1) {
            System.out.println("Setting heading and activating grabber");
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(5);
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(true);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(2, 250);

        } else if (newState == 2) {
            System.out.println("Rotating towards the goal.");
            this.fixed = false;
            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            double rot = calculateRotate(us);
            while (Math.abs(rot) >= 0.15) {
                us = Strategy.world.getRobot(RobotType.FRIEND_2);
                if (us != null) {
                    rot = calculateRotate(us);
                    double constant = 100 * rot;
                    if (Math.abs(constant) >= 70) {
                        constant = 70;
                        if (rot < 0) constant = -70;
                    }
                    if (Math.abs(constant) <= 40) {
                        constant = 40;
                        if (rot < 0) constant = -40;
                    }

                    System.out.println("rotation: " + rot + " constant: " + constant);
                    ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
                } else break;
            }
            this.robot.port.stop();
            this.fixed = true;

        } else if (newState == 3) {
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);
            Fred fred = (Fred) this.robot;
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    fred.KICKER_CONTROLLER.setActive(true);
//                    ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(255, -255, -255, 255);
//                }
//            };
//            timer.schedule(task, 500);
//            fred.KICKER_CONTROLLER.setActive(true);
            ((FredRobotPort) fred.port).kicker(1);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(60, -60, -60, 60);

            System.out.println("Moving forward now and kicking now. ");

        } else { // state 4
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);

        }
    }

    private double calculateRotate(Robot us) {
        EnemyGoal emGoal = new EnemyGoal();
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        double rotation = 0;
        VectorGeometry heading = new VectorGeometry(emGoal.getX(), emGoal.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
        System.out.println("rotation = " + rotation);
        return rotation;
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
        double angle = VectorGeometry.angle(0, 1, 1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

//        if (VectorGeometry.distance(this.ball.getX(), this.point.getY(), us.location.x, us.location.y) > 5) {
//            //Not sure how to make so that when this is called we start GoToBall again... maybe in Behave???
//            System.out.println("We are too close far away from the ball, stopping GoalKick");
//            this.enterState(4);
//        } else {

        if (fixed) {
            this.rotation = rotation;
            if (this.state != 3) this.enterState(3);
            return;
        } else {
            this.enterState(2);
        }
//        }
    }
}