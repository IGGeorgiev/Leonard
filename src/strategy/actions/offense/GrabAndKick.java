package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Keqi, Tommy and Isabella
 */
public class GrabAndKick extends ActionBase {
    private Ball ball, tempBall;
    private boolean fixed = false;
    private boolean kickingToGoal = false;
    private boolean grabbingBall = true;
    private EnemyGoal emgoal;
    private double rotation;
    private double distFromUsToBall;
    private Timer timer;
    private boolean grabbed = false;

    private int TOLERANCE_VALUE = 35;
    private int QUICK_STOP_TIMER = 500; // 2 seconds                    this.tempBall = Strategy.world.getBall();

    private int STOP_TIMER_1 = 1000; // 2 seconds
    private int STOP_TIMER_2 = 2000; // 2 seconds
    private double ROBOT_FACING_BALL = 0.15; // 2 seconds

    public GrabAndKick(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = "GOTO";
        emgoal = new EnemyGoal();
    }

    @Override
    public void enterState(int newState) {

        Robot us;
        if (newState == 1) {
            System.out.println("Starting GoToBall");
            System.out.println("GotoBall, Current State: " + this.state);

            this.robot.MOTION_CONTROLLER.setActive(true);
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);

        } else if (newState == 2) {
            System.out.println("GotoBall, Current State: " + this.state);
            double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
            Robot us1 = Strategy.world.getRobot(RobotType.FRIEND_2);
            double tempRotation = 0;
            if(us1 != null ) {
                VectorGeometry robotHeading = VectorGeometry.fromAngular(us1.location.direction + angle, 10, null);
                VectorGeometry heading = new VectorGeometry(tempBall.location.x, tempBall.location.y);
                VectorGeometry robotToPoint = VectorGeometry.fromTo(us1.location, heading);
                 tempRotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
            }

            System.out.println("rotation = " + tempRotation);

            this.robot.MOTION_CONTROLLER.setActive(false);
            double constant;
            constant = tempRotation * 30;
            if (Math.abs(constant) <= 30) {
                if (constant > 0) {
                    constant = 30;
                } else {
                    constant = -30;
                }
            }
            System.out.println("motor speed = " + constant);
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);


        } else if (newState == 3) {
            System.out.println("GotoBall, Current State: " + this.state);
            System.out.println("distFromUsToBall = " + distFromUsToBall);

            Fred fred = (Fred) this.robot;
            this.robot.MOTION_CONTROLLER.clearObstacles();
            this.robot.MOTION_CONTROLLER.setActive(false);



            System.out.println("MOVING FORWARD");
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //...Perform a task...
                    System.out.println("STOP TIMER");
                    TimerTask grabbedEvent = new TimerTask() {
                        @Override
                        public void run() {
                            grabbed = true;
                        }
                    };
                    java.util.Timer grabTimer = new java.util.Timer();
                    grabTimer.schedule(grabbedEvent, (long)QUICK_STOP_TIMER);
                    fred.GRABBER_CONTROLLER.grab(2, QUICK_STOP_TIMER);
                    ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(0, 0, 0, 0);
                }
            };
            timer = new Timer(1200, taskPerformer);
            timer.setRepeats(false);
            double constant = 80;
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, -constant, -constant, constant);
//            }
            timer.start();

        } else if (newState == 4) {
            System.out.println("GotoBall, Current State: " + this.state);
            System.out.println("distFromUsToBall = " + distFromUsToBall);

            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            this.robot.MOTION_CONTROLLER.setActive(true);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) us.location.x, (int) us.location.y));
            this.fixed = true;

        } else if (newState == 5) {
            System.out.println("GotoBall, Current State: " + this.state);
            System.out.println("kicking to goal");
            Fred fred = (Fred) this.robot;
            fred.MOTION_CONTROLLER.setActive(false);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(0, 0, 0, 0);

            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, STOP_TIMER_2);

            fred.KICKER_CONTROLLER.setActive(true);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(80, -80, -80, 80);
            fred.MOTION_CONTROLLER.setActive(true);


        } else {// state 6
            Fred leonard = (Fred) this.robot;

            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //...Perform a task...
                    System.out.println("TIMEOUT");
                    leonard.GRABBER_CONTROLLER.setActive(false);
                    leonard.KICKER_CONTROLLER.setActive(false);
                    leonard.MOTION_CONTROLLER.setDestination(null);
                    leonard.MOTION_CONTROLLER.setHeading(null);
                }
            };
//            if (timer != null && timer.isRunning()) timer.stop();
            timer = new Timer(STOP_TIMER_2, taskPerformer);
            timer.setRepeats(false);
            timer.start();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        ball = Strategy.world.getBall();
        if (ball!=null && Strategy.world.getProbableBallHolder()!=RobotType.FRIEND_2) {
            tempBall = ball;
        }
        VectorGeometry robotHeading;
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        VectorGeometry heading;
        VectorGeometry robotToPoint;
        if (us != null) {
            distFromUsToBall = VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y);

            robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            heading = new VectorGeometry(ball.location.x, ball.location.y);
            robotToPoint = VectorGeometry.fromTo(us.location, heading);
            this.rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
        }

        if (us == null) {
            return;
        } else if (this.state == 0) {
            // Go to point
            this.enterState(1);
        } else if (this.state == 1) {
            if (distFromUsToBall < TOLERANCE_VALUE) {
                ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, STOP_TIMER_2);
                if (Math.abs(rotation) >= ROBOT_FACING_BALL) { // check if state != 3 here???
                    this.enterState(2);
                    return;
                }
            }
        } else if (this.state == 2) {
            if (distFromUsToBall >= TOLERANCE_VALUE) {
                this.enterState(1);
                return;
            } else if (Math.abs(rotation) >= ROBOT_FACING_BALL) { // check if state != 3 here???
                if (distFromUsToBall < TOLERANCE_VALUE) {
                    ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, STOP_TIMER_2);
                }
                this.enterState(2);
                if (distFromUsToBall > TOLERANCE_VALUE) {
                    this.enterState(1);
                    return;
                }
            } else {
                this.enterState(3);
                return;
            }
        } else if (this.state == 3) {
//            if (Math.abs(rotation) >= 0.3) {
//                this.enterState(2);
//                return;
//            } else
            if (distFromUsToBall >= TOLERANCE_VALUE - 10) {
                this.enterState(1);
                return;
            } else if (grabbed) {
                grabbed = false;
                this.enterState(4);
                return;
            }
        } else if (this.state == 4) {
//            System.out.println("Goal kick");
            heading = new VectorGeometry(emgoal.getX(), emgoal.getY());
            robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            robotToPoint = VectorGeometry.fromTo(us.location, heading);
            double enemyRotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//            if (distFromUsToBall > TOLERANCE_VALUE) {
//                this.enterState(1);
//                return;
//            } else
            System.out.println("Rotation against enemy goal: "+ enemyRotation);
            if (Math.abs(enemyRotation) <= ROBOT_FACING_BALL) {
                this.enterState(5);
                return;
            }
        } else if (this.state == 5) {
            if (distFromUsToBall > 40) this.enterState(6);
        } else if (this.state == 6) {
            // halt
            Fred fred = (Fred) this.robot;
            fred.GRABBER_CONTROLLER.grab(2, STOP_TIMER_1);
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            throw new ActionException(true, false);
        }
    }
}




