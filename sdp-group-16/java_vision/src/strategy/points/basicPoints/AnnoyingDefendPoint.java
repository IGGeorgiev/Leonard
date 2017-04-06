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
public class AnnoyingDefendPoint extends DynamicPointBase {

    @Override
    public void recalculate() {
        VectorGeometry lower = new VectorGeometry(- Constants.PITCH_WIDTH/2 + 20, 20);
        VectorGeometry upper = new VectorGeometry(- Constants.PITCH_WIDTH/2 + 20, -20);
        Ball ball = Strategy.world.getBall();
        VectorGeometry closest = null;
        if(ball != null && ball.velocity.length() > 0.2){
            closest = VectorGeometry.vectorToClosestPointOnFiniteLine(lower, upper, ball.location);
            this.x = (int)closest.x;
            this.y = (int)closest.y;
        } else {
            Robot foe1 = Strategy.world.getRobot(RobotType.FOE_1);
            if(foe1 != null){
                closest = VectorGeometry.intersectionWithFiniteLine(foe1.location, VectorGeometry.fromAngular(foe1.location.direction, 10, null), lower, upper);
            }
            Robot foe2 = Strategy.world.getRobot(RobotType.FOE_2);
            if(foe2 != null){
                if(foe1 == null || VectorGeometry.angle(VectorGeometry.fromAngular(foe2.location.direction, 10, null), VectorGeometry.fromTo(foe2.location, new VectorGeometry(-Constants.PITCH_WIDTH, 0))) < VectorGeometry.angle(VectorGeometry.fromAngular(foe1.location.direction, 10, null), VectorGeometry.fromTo(foe1.location, new VectorGeometry(-Constants.PITCH_WIDTH, 0)))){
                    closest = VectorGeometry.intersectionWithFiniteLine(foe2.location, VectorGeometry.fromAngular(foe2.location.direction, 10, null), lower, upper);
                }
            }
            if(closest == null){
                this.x = - Constants.PITCH_WIDTH/2 + 20;
                this.y = 0;
            } else {
                this.x = (int)closest.x;
                this.y = (int)closest.y;
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
