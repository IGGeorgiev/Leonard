package strategy.navigation.aStarNavigation;

/**
 * Created by Simon Rovder
 */
public interface AStarObstacle {
    public boolean contains(int x, int y, int lastX, int lastY);
}
