package strategy.actions.offense;

import communication.ports.interfaces.RobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Simon Rovder
 */
public class OffensiveKick extends ActionBase {

    public OffensiveKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "OffensiveKick";
    }
    @Override
    public void enterState(int newState) {

        this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
        this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
        this.robot.MOTION_CONTROLLER.setTolerance(15);
        ((Fred)this.robot).KICKER_CONTROLLER.setActive(false);
        this.state = 0;
    }

    @Override
    public void tok() throws ActionException {

    }
}
