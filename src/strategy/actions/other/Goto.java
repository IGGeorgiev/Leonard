package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.Strategy;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Goto extends ActionBase {
    public Goto(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " GOTO";
    }

    @Override
    public void enterState(int newState) {
        if(newState == 1){
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        } else {
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null){
            this.enterState(0);
            return;
        }
        if(VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10){
            this.enterState(2);
        } else {
            if(this.state == 0){
                this.enterState(1);
            }
        }
        if(this.state == 2){
            throw new ActionException(true, false);
        }
    }
}




