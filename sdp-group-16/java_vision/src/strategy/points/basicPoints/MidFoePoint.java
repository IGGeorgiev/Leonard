package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class MidFoePoint extends DynamicPointBase {
    @Override
    public void recalculate() {
        Robot foe1Pos = Strategy.world.getRobot(RobotType.FOE_1);
        Robot foe2Pos = Strategy.world.getRobot(RobotType.FOE_2);

        if(foe1Pos != null && foe2Pos != null){
            this.x = ((int)foe1Pos.location.x + (int)foe2Pos.location.x)/2;
            this.y = ((int)foe1Pos.location.y + (int)foe2Pos.location.y)/2;
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}
