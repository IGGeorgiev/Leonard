package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.actions.offense.GoalKick;
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
    private VectorGeometry emgoal;
    private double rotation;
    private java.util.Timer timer = new java.util.Timer();

    public GrabAndKick(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = "GOTO";
    }

    @Override
    public void enterState(int newState) {

        Robot us;
        if (newState == 1) {
            System.out.println("moving my ass to that bloody point");
            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 20));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        }  else if (newState == 2) {
            System.out.println("yo fix rotation!");
            double constant = 300 * rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);

        } else if (newState == 3) {
            System.out.println("trying to move forward!!!");
            this.robot.MOTION_CONTROLLER.clearObstacles();
            // TODO: should move forward here
//            ((FredRobotPort)this.robot.port).grabber(1);
//            this.robot.ACTION_CONTROLLER.setAction(new Demo(robot, 150, 255, 255, 150));
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(255, -255, -255, 255);
            System.out.println("moving forward now! ");
            Fred fred = (Fred) this.robot;
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //...Perform a task...
                    fred.GRABBER_CONTROLLER.grab(2, 500);
                }
            };
            Timer tm = new Timer(1000, taskPerformer);
            tm.setRepeats(false);
            tm.start();
        } else if (newState == 4) {
            System.out.println("Rotating towards the goal.");
            us = Strategy.world.getRobot(RobotType.FRIEND_2);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) us.location.x, (int) us.location.y));
            this.fixed = true;

        } else if (newState == 5) {
            System.out.println("Moving forward now and kicking now. ");

            this.robot.MOTION_CONTROLLER.setActive(false);
            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);
            Fred fred = (Fred) this.robot;

            fred.KICKER_CONTROLLER.setActive(true);
            ((FredRobotPort) fred.port).kicker(1);
            ((FourWheelHolonomicRobotPort) fred.port).fourWheelHolonomicMotion(60, -60, -60, 60);
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
            timer.schedule(task, 3000);

        } else {// state 6

        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        ball = Strategy.world.getBall();


        double distFromUsToBall = VectorGeometry.distance(ball.location.x, ball.location.y, us.location.x, us.location.y);
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

        if (distFromUsToBall < 5) {
            System.out.println("Starting GoalKick");
            kickingToGoal = true;
            grabbingBall = false;
        }

        if (kickingToGoal) {
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

        if (grabbingBall) {
            if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10) {
                System.out.println("too close moving backwards!!!!");
                if (Math.abs(rotation) > 0.2) {
                    ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(-255, 255, 255, -255);
                }
            } else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 40) {
                System.out.println("================ hit tolerance!! ===========");

                ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);

                if (Math.abs(rotation) >= 0.1) {
                    this.rotation = rotation;
                    this.enterState(2);
                    return;
                } else {
                    this.enterState(3);
                }
            }
        }
    }
}




