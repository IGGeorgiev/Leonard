package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.robots.RobotBase;
/**
 * Created by Simon Rovder
 */
public class Stop extends ActionBase {
    public Stop(RobotBase robot) {
        super(robot);
        this.rawDescription = "STOP";
    }

    @Override
    public void enterState(int newState) {
        if(newState < 5){
            this.robot.port.stop();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        this.enterState(this.state + 1);
    }
}
