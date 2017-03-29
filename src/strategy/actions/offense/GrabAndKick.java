package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
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
    private VectorGeometry emgoal;
    private double rotation;
    private java.util.Timer timer;

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
        } else if (newState == 2) {
            //Adding this results in Leonard hesitating less when he goes to the ball.
            //This state is onlt used when we are far enough away from the ball due to lack of rotation speed.
            Ball ball = Strategy.world.getBall();
            EnemyGoal enemyGoal = new EnemyGoal();
            VectorGeometry ballVec = new VectorGeometry(ball.location.x, ball.location.y);
            VectorGeometry emgoal = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY());
            VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ballVec, 20);

            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) kickingPoint.x, (int) kickingPoint.y));
            this.robot.MOTION_CONTROLLER.setTolerance(5);

        } else if (newState == 3) {
            System.out.println("yo fix rotation!");
            double constant = 300 * rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);

        } else if (newState == 4) {
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
        } else if(newState == 5) {
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

        } else {// state 6

        }
        this.state = newState;
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
        double distFromUsToBall = VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y);
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) {
            // Go to point
            if (distFromUsToBall > 10) {
                //Gets there quicker
                this.enterState(2);
            } else {
                this.enterState(1);
            }
        } else if (this.state == 6) {
            // halt
            Fred fred = (Fred) this.robot;
            fred.GRABBER_CONTROLLER.grab(2, 300);
            this.enterAction(new GoalKick(this.robot), 0, 0);
        } else if (this.state == 3) {
            // move forward
        }

        if (distFromUsToBall < 5) {
            System.out.println("Starting GoalKick");
            kickingToGoal = true;
            this.enterState(5);
        }



        if (fixed) {
            this.rotation = rotation;
            if (this.state != 3) this.enterState(3);
            return;
        } else {
            this.enterState(2);
        }

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
                this.enterState(3);
                return;
            } else {
                this.enterState(4);
            }
        }
    }
}




