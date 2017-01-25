package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.ReverseBallDirection;
import strategy.robots.RobotBase;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class ShuntKick extends ActionBase {

    public ShuntKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "Shunt Kick";
    }


    private VectorGeometry destination;

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
            this.robot.MOTION_CONTROLLER.setHeading(new ReverseBallDirection());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {

    }
}
