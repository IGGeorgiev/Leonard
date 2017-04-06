package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Ball;
import vision.Robot;
import vision.constants.Constants;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class DangerousPoint extends DynamicPointBase {

    public static boolean FAR = false;


    @Override
    public void recalculate() {
        Ball ball = Strategy.world.getBall();
        if(ball != null){
            this.x = (int)ball.location.x;
            this.y = (int)ball.location.y;
        } else {
            VectorGeometry dangerous = null;
            Robot foe1 = Strategy.world.getRobot(RobotType.FOE_1);
            if(foe1 != null){
                dangerous = foe1.location.clone();
            }
            Robot foe2 = Strategy.world.getRobot(RobotType.FOE_2);
            if(foe2 != null && (foe1 == null || Strategy.world.getProbableBallHolder() != foe1.type)){
                VectorGeometry goal = new VectorGeometry(-Constants.PITCH_WIDTH, 0);
                if(FAR){
                    if(foe1 == null || (VectorGeometry.distance(goal, foe1.location) < VectorGeometry.distance(foe2.location, goal))){
                        dangerous = foe2.location.clone();
                    }
                } else {
                    if(foe1 == null || (VectorGeometry.distance(goal, foe1.location) > VectorGeometry.distance(foe2.location, goal))){
                        dangerous = foe2.location.clone();
                    }
                }
            }
            if(dangerous == null){
                this.x = - Constants.PITCH_WIDTH/2 + 20;
                this.y = 0;
            } else {
                this.x = (int)dangerous.x;
                this.y = (int)dangerous.y;
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
