package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 *
 * When the robot fails, it shall Contemplate...
 */
public class Contemplating extends ActionBase {
    public Contemplating(RobotBase robot) {
        super(robot);
        this.rawDescription = " Contemplating...";
    }

    @Override
    public void enterState(int newState) {
        this.robot.port.stop();
    }

    @Override
    public void tok() throws ActionException {}
}
