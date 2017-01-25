package strategy.navigation.aStarNavigation;

import strategy.Strategy;
import strategy.navigation.NavigationInterface;
import strategy.navigation.Obstacle;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class AStarNavigation implements NavigationInterface {


    private VectorGeometry heading = null;
    private VectorGeometry destination = null;
    private LinkedList<Obstacle> obstacles;
    private ObstacleField lastField;


    public AStarNavigation(){
        this.obstacles = new LinkedList<Obstacle>();
    }

    @Override
    public void setHeading(VectorGeometry heading){
        this.heading = heading;
    }


    @Override
    public void setObstacles(LinkedList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public void clearObstacles() {
        this.obstacles = null;
    }

    @Override
    public void setDestination(VectorGeometry destination) {
        this.destination = destination;
    }


    @Override
    public void draw() {
        ObstaclePreview.preview.updateField(this.lastField, destination);
        ObstaclePreview.preview.setWhite((int)this.destination.x, (int)this.destination.y);
        ObstaclePreview.preview.flush();
    }

    @Override
    public VectorGeometry getForce() {

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null) return null;

        ObstacleField field = ObstacleField.worldToObstacleField(Strategy.world);

        field.addObstacles(obstacles);
        AStarSearch navigator = new AStarSearch();
        navigator.setTarget(destination);

        VectorGeometry force = navigator.search(field, us.location);

        if(force == null) force = new VectorGeometry(0,0);

        this.lastField = field;

        return force;
    }
}
