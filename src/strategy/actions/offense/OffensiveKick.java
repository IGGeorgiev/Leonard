package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;

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
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(true);
            }
        }
        this.state = 0;
    }

    @Override
    public void tok() throws ActionException {

    }
}
