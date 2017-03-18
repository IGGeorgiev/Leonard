package strategy.actions.other;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.Strategy;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Simon Rovder
 */
public class GotoBall extends ActionBase {
    private double rotation;
    public GotoBall(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = "GOTO";
    }

    @Override
    public void enterState(int newState) {

        if (newState == 1) {
            System.out.println("moving my ass to that bloody point");
            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 20));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        } else if (newState == 3) {
            System.out.println("trying to move forward!!!");
            this.robot.MOTION_CONTROLLER.clearObstacles();
            // TODO: should move forward here
//            ((FredRobotPort)this.robot.port).grabber(1);
//            this.robot.ACTION_CONTROLLER.setAction(new Demo(robot, 150, 255, 255, 150));
            ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(255,-255,-255,255);
            System.out.println("moving forward now! ");
            Fred fred = (Fred)this.robot;
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

        }

        if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 5) {
            System.out.println("we are too close to that shit");
            this.enterState(2);
        }
//        else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 5) {
//            System.out.println("I am too close! ");
//            this.enterState(2);
//        }
        double angle = VectorGeometry.angle(0,1,-1,1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10) {
            System.out.println("too close moving backwards!!!!");
            if (Math.abs(rotation)>0.2) {
                ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(-255, 255, 255, -255);
            }
        }
        else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 40) {
            System.out.println("================ hit tolerance!! ===========");

            ((Fred)this.robot).GRABBER_CONTROLLER.grab(1, 2000);


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




