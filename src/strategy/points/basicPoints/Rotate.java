package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Rotate extends DynamicPointBase {

    public Rotate(){
        super();
    }

    @Override
    public void recalculate() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us != null){
            VectorGeometry location = us.location.clone();
            location.setLength(50);
            location.rotate(0.3);
            this.x = (int)location.x;
            this.y = (int)location.y;
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
