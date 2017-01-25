package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.WorldTools;
import strategy.points.DynamicPointBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.robotAnalysis.newRobotAnalysis.PatternMatcher;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class MidDangerPoint extends DynamicPointBase {

    private DangerousPoint danger = new DangerousPoint();

    private RobotType usType;

    public MidDangerPoint(RobotType robotType){
        this.usType = robotType;
    }

    @Override
    public void recalculate() {
        Ball b = Strategy.world.getBall();
        Robot us = Strategy.world.getRobot(this.usType);
        if (b == null || VectorGeometry.squareLength(b.velocity.x, b.velocity.y) < 0.1 || us == null) {
            this.danger.recalculate();
            VectorGeometry dangerV = new VectorGeometry(danger.getX(), danger.getY());
            VectorGeometry base;

            if (WorldTools.isPointInFriendDefenceArea(dangerV)) {
                base = VectorGeometry.vectorToClosestPointOnFiniteLine(new VectorGeometry(-Constants.PITCH_WIDTH / 2, 20), new VectorGeometry(-Constants.PITCH_WIDTH / 2, 20), dangerV);
            } else {
                base = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
            }
            VectorGeometry goal = base.minus(danger.getX(), danger.getY());
            goal.multiply(0.7);
            goal.add(danger.getX(), danger.getY());
            this.x = (int) (goal.x);
            this.y = (int) (goal.y);
        } else {
            VectorGeometry v = VectorGeometry.closestPointToLine(b.location, b.velocity, us.location);
//            VectorGeometry v = VectorGeometry.intersectionWithFiniteLine(b.location, b.velocity, new VectorGeometry(-Constants.PITCH_WIDTH/2, 30), new VectorGeometry(-Constants.PITCH_WIDTH/2, -30));
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
