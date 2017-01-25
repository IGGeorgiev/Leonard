package strategy.actions.defence;

import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.StatefulActionBase;
import strategy.actions.other.DefendGoal;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotAlias;
import vision.RobotType;

/**
 * Created by s1351669 on 14/01/17.
 */
public class CleverDefend extends StatefulActionBase<CleverDefend.Holder> {

    public enum Holder {
        JEFFREY, VENUS, OTHER
    }

    public CleverDefend(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = "Clever Defend";
    }

    @Override
    protected Holder getState() {
        RobotType holderType = Strategy.world.getProbableBallHolder();
        if (holderType == null) return Holder.OTHER;
        Robot holderRobot = Strategy.world.getRobot(holderType);
        if (holderRobot == null) return Holder.OTHER;
        if (holderRobot.alias == RobotAlias.JEFFREY) return Holder.JEFFREY;
        if (holderRobot.alias == RobotAlias.VENUS) return Holder.VENUS;
        return Holder.OTHER;

    }

    @Override
    public void enterState(int newState) {
        if (newState == 1){
            this.enterAction(new DefendGoal(this.robot), 0, 0);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        switch (this.nextState){
            case JEFFREY:
                // TODO: Add Jeffrey Defence
                break;
            case VENUS:
                // TODO: Add Venus Defence
                break;
            default:
                this.enterState(1);
                break;
        }
    }
}
