package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import communication.ports.interfaces.KickerEquipedRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;

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
        this.grabberIsDown = true;
        robotPort.kicker(0);
    }

    @Override
    public void perform() {
        assert (this.robot.port instanceof KickerEquipedRobotPort);
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        Ball ball = Strategy.world.getBall();
        if (us != null) {
            if (this.isActive()) {
                ((FredRobotPort) this.robot.port).kicker(1);
            }
        }
    }


}