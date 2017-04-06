package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
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
    private Ball ball;
    private boolean fixed = false;
    private boolean kickingToGoal = false;
    private boolean grabbingBall = true;
    private EnemyGoal emgoal;
    private double rotation;
    private double distFromUsToBall;
    private Timer timer;
    private boolean grabbed = false;

    private int TOLERANCE_VALUE = 30;
    private int QUICK_STOP_TIMER = 500; // 2 seconds
    private int STOP_TIMER_1 = 1000; // 2 seconds
    private int STOP_TIMER_2 = 2000; // 2 seconds
    private double FACING_ROBOT_ANGLE = 0.15; // 2 seconds

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

//            System.out.println("moving my ass to that bloody point");
            this.robot.MOTION_CONTROLLER.setActive(true);
//            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 10));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);

        } else if (newState == 2) {
            System.out.println("GotoBall, Current State: " + this.state);
            System.out.println("rotation = " + rotation);

            this.robot.MOTION_CONTROLLER.setActive(false);
//            System.out.println("yo fix rotation!");
            double constant;
            constant = rotation * 30;
//            System.out.println("Constant: " + constant);
            if (Math.abs(constant) <= 30) {
                if (rotation > 0) {
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


//            System.out.println("moving forward now! ");
            //POSSIBLE PID IMPLEMENTATION WITHOUT TIMERS
//            if (distFromUsToBall < 5) {
//                System.out.println("GRAB NOW");
//                fred.GRABBER_CONTROLLER.grab(2, QUICK_STOP_TIMER);
//                ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(0, 0, 0, 0);
//                grabbed = true;
//            } else {
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

//            System.out.println("Rotating towards the goal.");
            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            this.robot.MOTION_CONTROLLER.setActive(true);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) us.location.x, (int) us.location.y));
            this.fixed = true;

        } else if (newState == 5) {
            System.out.println("GotoBall, Current State: " + this.state);

//            System.out.println("Moving forward now and kicking now. ");

//            this.robot.MOTION_CONTROLLER.setActive(false);
//            System.out.println("raise the grabber up");
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, STOP_TIMER_2);
            Fred fred = (Fred) this.robot;

            fred.KICKER_CONTROLLER.setActive(true);
//            ((FredRobotPort) fred.port).kicker(1);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(80, -80, -80, 80);

            fred.MOTION_CONTROLLER.setActive(true);
//            fred.MOTION_CONTROLLER.setDestination(emgoal);
//            fred.MOTION_CONTROLLER.setHeading(emgoal);
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

        } else {// state 6

        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        ball = Strategy.world.getBall();
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
//            this.enterState(1);
            return;
        } else if (this.state == 0) {
            // Go to point
            this.enterState(1);
        } else if (this.state == 1) {
            if (distFromUsToBall < TOLERANCE_VALUE) {
//                System.out.println("LiftGrabber");
                ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, STOP_TIMER_2);
                if (Math.abs(rotation) >= FACING_ROBOT_ANGLE) { // check if state != 3 here???
                    this.enterState(2);
                    return;
                }
            }
        } else if (this.state == 2) {
            if (distFromUsToBall >= TOLERANCE_VALUE) {
                this.enterState(1);
                return;
            } else if (Math.abs(rotation) >= FACING_ROBOT_ANGLE) { // check if state != 3 here???
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
            if (distFromUsToBall >= TOLERANCE_VALUE) {
                this.enterState(1);
                return;
            } else if (grabbed) {
                grabbed = false;
                this.enterState(4);
                return;
            }
        } else if (this.state == 4) {
            System.out.println("Goal kick");
            heading = new VectorGeometry(emgoal.getX(), emgoal.getY());
            robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            robotToPoint = VectorGeometry.fromTo(us.location, heading);
            rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//            if (distFromUsToBall > TOLERANCE_VALUE) {
//                this.enterState(1);
//                return;
//            } else
            if (Math.abs(rotation) <= FACING_ROBOT_ANGLE) {
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
//        else if (this.state == 2 && Math.abs(rotation) >= 0.1) {
//            this.rotation = rotation;
//            this.enterState(2);
//            return;
//        }

//        if (grabbingBall) {
//            System.out.println("GotoBall, Current State: " + this.state);
//            System.out.println("distance = " + distFromUsToBall);
//            if (distFromUsToBall < 5) {
//                kickingToGoal = true;
//                grabbingBall = false;
//                return;
//            }
//
////            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) < 10) {
//////                System.out.println("too close moving backwards!!!!");
////                if (Math.abs(rotation) > 0.2) {
////                    ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(-255, 255, 255, -255);
////                }
////            } else
//            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) < 30) {
////                System.out.println("================ hit tolerance!! ===========");
//                if (state != 3) {
//                    System.out.println("LiftGrabber");
//                    ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);
//                }
//                System.out.println("Math.abs(rotation) = " + Math.abs(rotation));
//                if (state != 3 && Math.abs(rotation) >= 0.15) { // check if state != 3 here???
//                    this.rotation = rotation;
//                    this.enterState(2);
//                    return;
//                } else {
//                    this.enterState(3);
//                }
//            } else {
//                this.enterState(1);
//            }
//        }

//        if (kickingToGoal) {
//            System.out.println("GoalKick");
//            heading = new VectorGeometry(emgoal.getX(), emgoal.getY());
//            robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
//            robotToPoint = VectorGeometry.fromTo(us.location, heading);
//            rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//            if (VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y) > 20) {
//                kickingToGoal = false;
//                grabbingBall = true;
//                this.enterState(1);
//            } else if (Math.abs(rotation) >= 0.15) {
//                this.rotation = rotation;
//                this.enterState(4);
//                return;
//            } else {
//                if (this.state != 5) this.enterState(5);
//            }
//        }
    }
}




