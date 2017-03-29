package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.Timer;
import java.util.TimerTask;


public class GoalKick extends ActionBase {


    private Ball ball;
    private boolean fixed = false;
    private VectorGeometry emgoal;
    private double rotation;
    private Timer timer= new Timer();
    private double previousConstant=0;

    public GoalKick(RobotBase robot) {
        super(robot, new EnemyGoal());
        this.rawDescription = "GoalKick";
    }

    @Override
    public void enterState(int newState) {

        Strategy.world.getRobot(RobotType.FRIEND_2);
        Robot us;
//
        this.state = newState;

        if (newState == 1) {
            System.out.println("Setting heading and activating grabber");
//
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(true);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
//            ((Fred) this.robot).GRABBER_CONTROLLER.grab(2, 250);

        } else if (newState == 2) {
            System.out.println("Rotating towards the goal.");
            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int)us.location.x, (int)us.location.y));
            this.fixed = true;

        } else if (newState == 3) {
            this.robot.MOTION_CONTROLLER.setActive(false);
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);
            Fred fred = (Fred) this.robot;
            fred.KICKER_CONTROLLER.setActive(true);
            ((FredRobotPort) fred.port).kicker(1);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(60, -60, -60, 60);
             Fred leonard = (Fred)this.robot;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("TIMEOUT");
                    leonard.GRABBER_CONTROLLER.setActive(false);
                    leonard.KICKER_CONTROLLER.setActive(false);
                    leonard.MOTION_CONTROLLER.setDestination(null);
                    leonard.MOTION_CONTROLLER.setHeading(null);
//                    throw new ActionException(true, false)
                }
            };
            timer.schedule(task, 3000);

            System.out.println("Moving forward now and kicking now. ");

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
        }

        //The variable point is set in the super(robot, new EnemyGoal()) line in the class initializer.
        double angle = VectorGeometry.angle(0, 1, 1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
        ball = Strategy.world.getBall();

//        if (ball != null && this.state == 3 && VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) > 40) {
//            //Not sure how to make so that when this is called we start GoToBall again... maybe in Behave???
//            System.out.println("We are too close far away from the ball, stopping GoalKick");
//
//        }

        if (Math.abs(rotation)>=0.15) {
            this.rotation = rotation;
            this.enterState(2);
            return;
        } else {
            if (this.state != 3) this.enterState(3);
        }
//        }
    }
}