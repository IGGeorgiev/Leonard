package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
/**
 * Created by Simon Rovder
 */
public class HoldPosition extends ActionBase {
    public HoldPosition(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " Hold Position";
    }

    @Override
    public void enterState(int newState) {

    }

    @Override
    public void tok() throws ActionException {
        this.robot.MOTION_CONTROLLER.setDestination(this.point);
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
    }
}
