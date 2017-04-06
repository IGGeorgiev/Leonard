package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class ReverseBallDirection extends DynamicPointBase {
    private BallPoint point = new BallPoint();

    @Override
    public void recalculate() {
        this.point.recalculate();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us != null){
            VectorGeometry ballDir = VectorGeometry.fromTo(us.location.x, us.location.y, this.point.getX(), this.point.getY()).multiply(-1);
            ballDir.plus(us.location);
            this.x = (int)ballDir.x;
            this.y = (int)ballDir.y;
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
