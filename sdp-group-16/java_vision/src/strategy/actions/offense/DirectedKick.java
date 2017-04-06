package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class DirectedKick extends ActionBase {

    public DirectedKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "OffensiveKick";
    }
    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
        this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).GRABBER_CONTROLLER.setActive(true);
            }
        }
        this.state = 0;
    }

    @Override
    public void tok() throws ActionException {

    }
}
