package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotAlias;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by s1351669 on 17/01/17.
 */
public class RobotPoint extends DynamicPointBase{

    private RobotType type = null;
    private RobotAlias alias = null;

    public RobotPoint(RobotType type){
        this.type = type;
    }

    public RobotPoint(RobotAlias alias){
        this.alias = alias;
    }


    @Override
    public void recalculate() {
        Robot r;

        if(this.alias == null) r = Strategy.world.getRobot(this.type);
        else r = Strategy.world.getRobot(this.alias);

        if(r != null){
            this.x = (int) r.location.x;
            this.y = (int) r.location.y;

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
