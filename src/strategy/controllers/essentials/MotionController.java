package strategy.controllers.essentials;

import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.offense.OffensiveKick;
import strategy.controllers.ControllerBase;
import strategy.navigation.NavigationInterface;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.navigation.aStarNavigation.AStarNavigation;
import strategy.navigation.potentialFieldNavigation.PotentialFieldNavigation;
import strategy.robots.RobotBase;
import strategy.GUI;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


import communication.ports.interfaces.FourWheelHolonomicRobotPort;

/**
 * Created by Simon Rovder
 */
public class MotionController extends ControllerBase {

    public MotionMode mode;
    private DynamicPoint heading = null;
    private DynamicPoint destination = null;

    private int tolerance;

    private LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();

    public MotionController(RobotBase robot) {
        super(robot);
    }

    public enum MotionMode{
        ON, OFF
    }

    public void setMode(MotionMode mode){
        this.mode = mode;
    }

    public void setTolerance(int tolerance){
        this.tolerance = tolerance;
    }

    public void setDestination(DynamicPoint destination){
        this.destination = destination;
    }

    public void setHeading(DynamicPoint dir){
        this.heading = dir;
    }

    public void addObstacle(Obstacle obstacle){
        this.obstacles.add(obstacle);
    }

    public void clearObstacles(){
        this.obstacles.clear();
    }

    public void perform(){
        if(this.mode == MotionMode.OFF) return;

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null) return;

        NavigationInterface navigation;

        VectorGeometry heading = null;
        VectorGeometry destination = null;



        if(this.destination != null) {
            this.destination.recalculate();

            destination = new VectorGeometry(this.destination.getX(), this.destination.getY());

            boolean intersects = false;


            for (Obstacle o : this.obstacles) {
                intersects = intersects || o.intersects(us.location, destination);
            }

            for (Robot r : Strategy.world.getRobots()) {
                if (r != null && r.type != RobotType.FRIEND_2) {
                    intersects = intersects || VectorGeometry.vectorToClosestPointOnFiniteLine(us.location, destination, r.location).minus(r.location).length() < 30;
                }
            }

            if (intersects || us.location.distance(destination) > 30) {
                navigation = new AStarNavigation();
                GUI.gui.searchType.setText("A*");
            } else {
                navigation = new PotentialFieldNavigation();
                GUI.gui.searchType.setText("Potential Fields");
            }

            navigation.setDestination(new VectorGeometry(destination.x, destination.y));


        } else {
//            navigation = new PotentialFieldNavigation();
//            GUI.gui.searchType.setText("Potential Fields");
//            destination = new VectorGeometry(us.location.x, us.location.y);
//            navigation.setDestination(new VectorGeometry(us.location.x, us.location.y));
            return;
        }

        if(this.heading != null){
            this.heading.recalculate();
            heading = new VectorGeometry(this.heading.getX(), this.heading.getY());
        } else heading = VectorGeometry.fromAngular(us.location.direction, 10, null);



        if(this.obstacles != null){
            navigation.setObstacles(this.obstacles);
        }



        VectorGeometry force = navigation.getForce();
        if(force == null){
            this.robot.port.stop();
            return;
        }


        double angle = VectorGeometry.angle(0,1,-1,1); // 45 degrees
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double factor = 1;
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        // Can throw null without check because null check takes SourceGroup into consideration.
        if(destination.distance(us.location) < 30){
            factor = 1.0;
        }

        if(this.destination != null && us.location.distance(destination) < tolerance && Math.abs(rotation)<0.1){
            this.robot.port.stop();

//            double constant;
//            while (Math.abs(rotation) >= 0.2) {
//                us = Strategy.world.getRobot(RobotType.FRIEND_2);
//                robotHeading = VectorGeometry.fromAngular(us.location.direction, 10, null);
//                robotToPoint = VectorGeometry.fromTo(us.location, heading);
//                rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//                constant = 100 * rotation;
//                System.out.println("rotation: " + rotation + " constant: " + constant);
//                ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(constant,constant,constant,constant);
//            }
//            this.robot.port.stop();
            this.robot.MOTION_CONTROLLER.clearObstacles();
            ActionListener task2 = evt -> {
                robot.MOTION_CONTROLLER.clearObstacles();
                robot.ACTION_CONTROLLER.setAction(new OffensiveKick(robot));
            };
            System.out.println("Should be heading the ball now and stopped");
            Timer tm2 = new Timer(1000, task2);
            tm2.setRepeats(false);
            tm2.start();

            return;
        }


//        strategy.navigationInterface.draw();
        this.robot.drive.move(this.robot.port, us.location, force, rotation, factor);

    }
}
