package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import communication.ports.interfaces.KickerEquipedRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Created by group 15 2017
public class KickerController extends ControllerBase {

    private boolean grabberIsDown;
    private FredRobotPort robotPort;

    public KickerController(RobotBase robot) {
        super(robot);
        this.grabberIsDown = true;
        robotPort = (FredRobotPort) robot.port;
        robotPort.kicker(0);

    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (active == false) robotPort.kicker(0);
        this.grabberIsDown = true;
    }

    @Override
    public void perform() {
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        if(us != null) {
            assert (this.robot.port instanceof KickerEquipedRobotPort);
            Ball ball = Strategy.world.getBall();
            EnemyGoal emgal = new EnemyGoal();
            double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
            VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, new VectorGeometry(emgal.getX(), emgal.getY()));
            double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//            && us.location.distance(ball.location) < 30
            if (Math.abs(rotation) < 0.2 ) {
                this.setActive(true);
                if (this.isActive()) {
                    ((FredRobotPort) this.robot.port).kicker(1);
                }
            } else {
                this.setActive(false);
                ((FredRobotPort) this.robot.port).kicker(0);
            }
        }
    }


}