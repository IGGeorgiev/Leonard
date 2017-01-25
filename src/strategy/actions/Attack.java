package strategy.actions;

import strategy.Strategy;
import strategy.Status;
import strategy.actions.other.Goto;
import strategy.actions.other.HoldPosition;
import strategy.actions.other.Stop;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.MidFoePoint;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class Attack extends StatefulActionBase<Status.BallState> {
    public Attack(RobotBase robot, DynamicPoint point) {
        super(robot, point);
    }

    @Override
    protected Status.BallState getState() {
        return Strategy.status.ballState;
    }


    @Override
    public void enterState(int newState) {
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        switch(Strategy.status.ballState){
            case THEM:
                this.enterAction(new HoldPosition(this.robot, new MidFoePoint()), 0, 0);
                break;
            case FREE:
                this.enterAction(new Goto(this.robot, new BallPoint()), 0, 0);
                break;
            case ME:
//                    if(Fred.FRED.hasBall()){
                this.enterAction(new Goto(this.robot, new BallPoint()), 0, 0);
                break;
            case FRIEND:
                this.enterAction(new Stop(null), 0, 0);
                this.delay(1500);
                break;
            case LOST:
                this.enterAction(new Stop(null), 0, 0);
                this.delay(1500);
                break;
        }
    }
}




