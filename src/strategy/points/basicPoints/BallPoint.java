package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class BallPoint extends DynamicPointBase {


    @Override
    public void recalculate() {
        Ball ball = Strategy.world.getBall();
        if(ball != null){
            this.x = (int)ball.location.x;
            this.y = (int)ball.location.y;
        } else {
            RobotType probableHolder = Strategy.world.getProbableBallHolder();
            if(probableHolder != null){
                Robot p = Strategy.world.getRobot(probableHolder);
                if(p != null){
                    this.x = (int)p.location.x;
                    this.y = (int)p.location.y;
                }
            }
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
