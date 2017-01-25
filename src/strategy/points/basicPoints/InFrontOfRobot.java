package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPoint;
import strategy.points.DynamicPointBase;
import vision.DynamicWorld;
import vision.Robot;
import vision.RobotAlias;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by s1351669 on 17/01/17.
 */
public class InFrontOfRobot extends DynamicPointBase {

    private RobotType type = null;
    private RobotAlias alias = null;

    public InFrontOfRobot(RobotType type){
        this.type = type;
    }

    public InFrontOfRobot(RobotAlias alias){
        this.alias = alias;
    }

    @Override
    public void recalculate() {
        Robot r;

        if(this.alias == null) r = Strategy.world.getRobot(this.type);
        else r = Strategy.world.getRobot(this.alias);

        if(r != null){
            VectorGeometry v = r.location.clone();
            v.add((new VectorGeometry()).fromAngular(r.location.direction, 40));
            this.x = (int) v.x;
            this.y = (int) v.y;

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
