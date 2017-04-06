package strategy.navigation.potentialFieldNavigation.fieldsources;

import strategy.navigation.potentialFieldNavigation.FieldFormula;
import strategy.navigation.potentialFieldNavigation.PotentialSource;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class LineSource extends PotentialSource {

    private final VectorGeometry a;
    private final VectorGeometry b;

    public LineSource(VectorGeometry a, VectorGeometry b, boolean attract, FieldFormula formula){
        super(formula, attract);
        this.a = a;
        this.b = b;
    }


//    public LineSource(VectorGeometry a, VectorGeometry b, double minX, double yFactor, boolean attract, double xCutoff, double yShift){
//        super(minX, yFactor, attract, xCutoff,yShift);
//        this.a = a;
//        this.b = b;
//    }

    @Override
    public VectorGeometry getForce(VectorGeometry point) {
        return this.relativePointToForce(this.getRelativePoint(point));
    }

    @Override
    public VectorGeometry getRelativePoint(VectorGeometry point) {
        return VectorGeometry.vectorToClosestPointOnFiniteLine(this.a, this.b, point).minus(point).multiply(-1);
    }

}
