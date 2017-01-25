package strategy.navigation.potentialFieldNavigation;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public interface PotentialSourceInterface {
    VectorGeometry getForce(VectorGeometry point);
    VectorGeometry getRelativePoint(VectorGeometry point);
    double getPotentialAtPoint(VectorGeometry relativePoint);
}
