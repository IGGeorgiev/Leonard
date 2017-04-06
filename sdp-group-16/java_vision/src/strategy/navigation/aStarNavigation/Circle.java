package strategy.navigation.aStarNavigation;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Circle implements AStarObstacle {

    private final int radius;
    private final int centerX;
    private final int centerY;

    public Circle(int centerX, int centerY, int radius){
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius  = radius;
    }

    @Override
    public boolean contains(int x, int y, int lastX, int lastY) {

        return VectorGeometry.distance(x, y, this.centerX, this.centerY) < radius &&
                VectorGeometry.distance(x, y, this.centerX, this.centerY) < VectorGeometry.distance(lastX, lastY, this.centerX, this.centerY);
    }

    public static void main(String [] args){
//        Circle c = new Circle(5,5,3);
//        System.out.println(c.contains(5,5));
//        System.out.println(c.contains(5,6));
//        System.out.println(c.contains(8,6));
    }
}
