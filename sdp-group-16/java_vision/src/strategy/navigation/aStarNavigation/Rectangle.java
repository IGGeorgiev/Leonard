package strategy.navigation.aStarNavigation;


import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Rectangle implements AStarObstacle {


    private final VectorGeometry a;
    private final VectorGeometry b;

    public Rectangle(VectorGeometry a, VectorGeometry b){
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean contains(int x, int y, int lastX, int lastY) {
        if(x == lastX && y == lastY) return false;
        if(x > a.x && x < b.x && y < a.y && y > b.y){
            return a.distance(x,y) <= a.distance(lastX, lastY) ||
                    b.distance(x,y) <= b.distance(lastX, lastY);
        }
        return false;
    }

    public static void main(String [] args){
        Rectangle r = new Rectangle(new VectorGeometry(-10,10), new VectorGeometry(10,-10));
        System.out.println(r.contains(0,0,0,0));
        System.out.println(r.contains(-5,-5,0,0));
        System.out.println(r.contains(0,0,-5,-5));
    }
}

