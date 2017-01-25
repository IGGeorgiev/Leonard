package strategy.points;

import strategy.points.basicPoints.ConstantPoint;
import vision.constants.Constants;

/**
 * Created by Simon Rovder
 * SDP2017NOTE
 * Extend this class to create more points.
 */
public abstract class DynamicPointBase implements DynamicPoint {
    protected int x;
    protected int y;

    public static DynamicPoint getEnemyGoalPoint(){
        return new ConstantPoint(Constants.PITCH_WIDTH/2, 0);
    }
}
