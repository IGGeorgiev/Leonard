package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class Waiting extends ActionBase {
    public Waiting(RobotBase robot) {
        super(robot);
        this.rawDescription = " Waiting.";
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            this.robot.MOTION_CONTROLLER.clearObstacles();
            this.robot.MOTION_CONTROLLER.setHeading(null);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {

    }
}
