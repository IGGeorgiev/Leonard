package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.DangerousPoint;
import strategy.points.basicPoints.MidDangerPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class DefendGoal extends ActionBase {


    public DefendGoal(RobotBase robot) {
        super(robot);
        this.rawDescription = " Defend Goal";
    }
    @Override
    public void enterState(int newState) {
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).GRABBER_CONTROLLER.setActive(false);
                ((Fred)this.robot).KICKER_CONTROLLER.setActive(false);
            }
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        if(this.state == 0){
            robot.MOTION_CONTROLLER.setHeading(new DangerousPoint());
            this.enterAction(new HoldPosition(this.robot, new MidDangerPoint(this.robot.robotType)), 0, 0);
            this.enterState(1);
        }
    }
}
