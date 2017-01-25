package strategy.points.basicPoints;

import strategy.points.DynamicPointBase;

/**
 * Created by Simon Rovder
 */
public class ConstantPoint extends DynamicPointBase {

    public ConstantPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void recalculate() {

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
