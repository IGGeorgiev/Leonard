package strategy.points.basicPoints;

import strategy.points.DynamicPoint;
import strategy.points.DynamicPointBase;
import vision.tools.VectorGeometry;

/**
 * Created by s1351669 on 17/01/17.
 */
public class MidPoint extends DynamicPointBase {

    private final DynamicPoint point1;
    private final DynamicPoint point2;

    public MidPoint(DynamicPoint point1, DynamicPoint point2){
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public void recalculate() {
        this.point1.recalculate();
        this.point2.recalculate();
        VectorGeometry v = VectorGeometry.fromTo(this.point1.getX(), this.point1.getY(), this.point2.getX(), this.point2.getY()).multiply(0.5).add(this.point1.getX(), this.point1.getY());
        this.x = (int) v.x;
        this.y = (int) v.y;
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
