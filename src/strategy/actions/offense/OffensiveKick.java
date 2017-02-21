package strategy.actions.offense;

import communication.ports.interfaces.RobotPort;
import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;

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
        this.robot.MOTION_CONTROLLER.setTolerance(20);
        ((Fred)this.robot).GRABBER_CONTROLLER.setActive(false);
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).GRABBER_CONTROLLER.setActive(false);
            }
        }
        this.state = 0;
    }

    @Override
    public void tok() throws ActionException {

    }
}
