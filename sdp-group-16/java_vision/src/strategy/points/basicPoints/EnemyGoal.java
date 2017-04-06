package strategy.points.basicPoints;

import strategy.points.DynamicPointBase;
import vision.constants.Constants;

/**
 * Created by Simon Rovder
 */
public class EnemyGoal extends DynamicPointBase {
    @Override
    public void recalculate() {

    }

    @Override
    public int getX() {
        return Constants.PITCH_WIDTH/2;
    }

    @Override
    public int getY() {
        return 0;
    }
}
