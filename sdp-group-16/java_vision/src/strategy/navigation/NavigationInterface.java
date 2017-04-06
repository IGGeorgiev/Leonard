package strategy.navigation;

import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public interface NavigationInterface {
    public void setObstacles(LinkedList<Obstacle> obstacles);
    public void clearObstacles();
    public void setDestination(VectorGeometry destination);
    public void setHeading(VectorGeometry destination);
    public VectorGeometry getForce();
    public void draw();
}
