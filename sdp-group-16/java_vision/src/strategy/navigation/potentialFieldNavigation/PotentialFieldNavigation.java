package strategy.navigation.potentialFieldNavigation;

import strategy.Strategy;
import strategy.navigation.NavigationInterface;
import strategy.navigation.Obstacle;
import strategy.navigation.potentialFieldNavigation.fieldsources.PointSource;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class PotentialFieldNavigation implements NavigationInterface {
    private VectorGeometry heading = null;
    private VectorGeometry destination = null;
    private LinkedList<Obstacle> obstacles;
    private PotentialField lastField;


    @Override
    public String toString(){
        return (this.destination != null ? this.destination.getClass().toString() : "NULL") + " - " + (this.heading != null ? this.heading.getClass().toString() : "NULL");
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
        PotentialPreview.preview.updateField(this.lastField);
    }

    @Override
    public VectorGeometry getForce() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null) return null;

        PotentialField field = PotentialField.worldToPotentialField(Strategy.world);


        field.addSource(new PointSource(this.destination, true, FieldFormula.ONE_OVER_X));
        field.setObstacles(this.obstacles);

        this.lastField = field;

        return field.getForce(us.location);


    }
}
