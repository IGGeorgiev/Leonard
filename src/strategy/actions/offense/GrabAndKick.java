package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.actions.offense.GoalKick;
import strategy.actions.other.GotoBall;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Simon Rovder
 */
public class GrabAndKick extends ActionBase {
    private Ball ball;
    private boolean fixed = false;
    private boolean kickingToGoal = false;
    private boolean grabbingBall = true;
    private EnemyGoal emgoal;
    private double rotation;
    private double distFromUsToBall;
    private Timer timer;

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

//            System.out.println("moving my ass to that bloody point");
            this.robot.MOTION_CONTROLLER.setActive(true);
//            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 10));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);

        } else if (newState == 2) {
            this.robot.MOTION_CONTROLLER.setActive(false);
            System.out.println("yo fix rotation!");
            double constant;
            constant = rotation * 50;
            System.out.println("Constant: " + constant);
            if (Math.abs(constant) <= 30) {
                if (rotation > 0) {
                    constant = 30;
                } else {
                    constant = -30;
                }
            }

            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);

        } else if (newState == 3) {
            Fred fred = (Fred) this.robot;
            this.robot.MOTION_CONTROLLER.clearObstacles();
            this.robot.MOTION_CONTROLLER.setActive(false);


            System.out.println("moving forward now! ");
            //POSSIBLE PID IMPLEMENTATION WITHOUT TIMERS
            if (distFromUsToBall < 10) {
                fred.GRABBER_CONTROLLER.grab(2, 2000);
                ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(0, 0, 0, 0);

            } else {
                double constant = distFromUsToBall * 50;
                System.out.println("Constant: " + constant);
                if (Math.abs(constant) <= 30) {
                    if (rotation > 0) {
                        constant = 30;
                    } else {
                        constant = -30;
                    }
                }
                ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, -constant, -constant, constant);
            }


//            ActionListener taskPerformer = new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    //...Perform a task...
//                    System.out.println("STOP TIMER");
//
//                    fred.GRABBER_CONTROLLER.grab(2, 2000);
//                    ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(0, 0, 0, 0);
//                }
//            };
//            if (timer != null) {
//                timer.stop();
//            }
//            timer = new Timer(1250, taskPerformer);
//            timer.setRepeats(false);
//            timer.start();
        } else if (newState == 4) {
            System.out.println("Rotating towards the goal.");
            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            this.robot.MOTION_CONTROLLER.setActive(true);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) us.location.x, (int) us.location.y));
            this.fixed = true;

        } else if (newState == 5) {
            System.out.println("Moving forward now and kicking now. ");

            this.robot.MOTION_CONTROLLER.setActive(false);
            System.out.println("raise the grabber up");
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 3000);
            Fred fred = (Fred) this.robot;

            fred.KICKER_CONTROLLER.setActive(true);
            ((FredRobotPort) fred.port).kicker(1);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(80, -80, -80, 80);
            Fred leonard = (Fred) this.robot;
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
            timer.stop();
            timer = new Timer(3000, taskPerformer);
            timer.setRepeats(false);
            timer.start();

        } else {// state 6

        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        ball = Strategy.world.getBall();


        distFromUsToBall = VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y);
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(ball.location.x, ball.location.y);
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) {
            // Go to point
            this.enterState(1);
        } else if (this.state == 6) {
            // halt
            Fred fred = (Fred) this.robot;
            fred.GRABBER_CONTROLLER.grab(2, 300);
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            throw new ActionException(true, false);
        }
//        else if (this.state == 2 && Math.abs(rotation) >= 0.1) {
//            this.rotation = rotation;
//            this.enterState(2);
//            return;
//        }

        if (grabbingBall) {
            System.out.println("GotoBall, Current State: " + this.state);
            System.out.println("distance = " + distFromUsToBall);
            if (distFromUsToBall < 5) {
                kickingToGoal = true;
                grabbingBall = false;
                return;
            }

//            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) < 10) {
////                System.out.println("too close moving backwards!!!!");
//                if (Math.abs(rotation) > 0.2) {
//                    ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(-255, 255, 255, -255);
//                }
//            } else
            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) < 30) {
//                System.out.println("================ hit tolerance!! ===========");
                if (state != 3) {
                    System.out.println("LiftGrabber");
                    ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);
                }
                System.out.println("Math.abs(rotation) = " + Math.abs(rotation));
                if (state != 3 && Math.abs(rotation) >= 0.3) { // check if state != 3 here???
                    this.rotation = rotation;
                    this.enterState(2);
                    return;
                } else {
                    this.enterState(3);
                }
            } else {
                this.enterState(1);
            }
        }

        if (kickingToGoal) {
            System.out.println("GoalKick");
            heading = new VectorGeometry(emgoal.getX(), emgoal.getY());
            robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            robotToPoint = VectorGeometry.fromTo(us.location, heading);
            rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) > 20) {
                kickingToGoal = false;
                grabbingBall = true;
                this.enterState(1);
            } else if (Math.abs(rotation) >= 0.15) {
                this.rotation = rotation;
                this.enterState(4);
                return;
            } else {
                if (this.state != 5) this.enterState(5);
            }
        }
    }
}




