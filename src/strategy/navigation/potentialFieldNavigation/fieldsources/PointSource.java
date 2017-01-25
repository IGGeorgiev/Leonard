package strategy.navigation.potentialFieldNavigation.fieldsources;

import strategy.navigation.potentialFieldNavigation.FieldFormula;
import strategy.navigation.potentialFieldNavigation.PotentialSource;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class PointSource extends PotentialSource {

    private final VectorGeometry point;

    public PointSource(VectorGeometry point, boolean attract, FieldFormula formula){
        super(formula, attract);
        this.point   = point;
    }

//    public PointSource(VectorGeometry point, double xStart, double xFactor, boolean attract, double xCutoff, double yIntercept){
//        super(xStart, xFactor, attract, xCutoff, yIntercept);
//        this.point   = point;
//    }

    @Override
    public VectorGeometry getForce(VectorGeometry point) {
        VectorGeometry relativePoint = this.getRelativePoint(point);
        this.relativePointToForce(relativePoint);
        return relativePoint;
    }

    @Override
    public VectorGeometry getRelativePoint(VectorGeometry point) {
        return point.copyInto(new VectorGeometry()).minus(this.point);
    }

}
