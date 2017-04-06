package strategy.navigation;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Obstacle {
    public final int x;
    public final int y;
    public final int radius;

    public Obstacle(int x, int y, int radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean intersects(VectorGeometry a, VectorGeometry b){
        return VectorGeometry.vectorToClosestPointOnFiniteLine(a, b, new VectorGeometry(this.x, this.y)).minus(x, y).length() < radius;
    }
}
