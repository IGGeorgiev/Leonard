package strategy.navigation.potentialFieldNavigation;

import strategy.navigation.Obstacle;
import strategy.navigation.potentialFieldNavigation.fieldsources.LineSource;
import strategy.navigation.potentialFieldNavigation.fieldsources.PointSource;
import strategy.WorldTools;
import vision.DynamicWorld;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;
/**
 * Created by Simon Rovder
 */
public class PotentialField {
    private LinkedList<PotentialSourceInterface> sources;

    public PotentialField(){
        this.sources = new LinkedList<PotentialSourceInterface>();
    }

    public void addSource(PotentialSourceInterface source){
        this.sources.add(source);
    }

    public double getPotentialAtPoint(VectorGeometry point){
        double d = 0;
        for(PotentialSourceInterface source : this.sources) d = d + source.getPotentialAtPoint(source.getRelativePoint(point));
        return d;
    }

    public VectorGeometry getForce(VectorGeometry point){
        VectorGeometry force = new VectorGeometry(0,0);
        for(PotentialSourceInterface source : this.sources) force.add(source.getForce(point));
        return force;
    }


    public static void main(String [] args){
        PotentialField field = new PotentialField();
        System.out.println(field.getForce(new VectorGeometry(1,0)));

    }



    private static final FieldFormula WALL_FIELD_FORMULA = FieldFormula.ONE_OVER_X2;
    private static final int WALL_Y_FACTOR = 1;
    private static final int WALL_SIZE = 20;

    public PotentialField addWalls(){
        double halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
        double halfHeight = vision.constants.Constants.PITCH_HEIGHT/2;
        this.addSource(new LineSource(new VectorGeometry(halfWidth, halfHeight), new VectorGeometry(halfWidth, -halfHeight), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        this.addSource(new LineSource(new VectorGeometry(halfWidth, -halfHeight), new VectorGeometry(-halfWidth, -halfHeight), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        this.addSource(new LineSource(new VectorGeometry(-halfWidth, -halfHeight), new VectorGeometry(-halfWidth, halfHeight), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        this.addSource(new LineSource(new VectorGeometry(-halfWidth, halfHeight), new VectorGeometry(halfWidth, halfHeight), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        return this;
    }

    public PotentialField addEnemyDefence(){
        double halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
//        this.addSource(new LineSource(new VectorGeometry(halfWidth, 60), new VectorGeometry(halfWidth - 60, 60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
//        this.addSource(new LineSource(new VectorGeometry(halfWidth, -60), new VectorGeometry(halfWidth - 60, -60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
//        this.addSource(new LineSource(new VectorGeometry(halfWidth - 60, 60), new VectorGeometry(halfWidth - 60, -60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        this.addSource(new LineSource(new VectorGeometry(halfWidth - 30, 30), new VectorGeometry(halfWidth - 30, -30), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE + 15).setYFactor(WALL_Y_FACTOR));
        return this;
    }

    public PotentialField addFriendDefence(){
        double halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
        this.addSource(new LineSource(new VectorGeometry(-halfWidth + 30, 30), new VectorGeometry(-halfWidth + 30, -30), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE + 15).setYFactor(WALL_Y_FACTOR));
//        this.addSource(new LineSource(new VectorGeometry(-halfWidth, 60), new VectorGeometry(-halfWidth + 60, 60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
//        this.addSource(new LineSource(new VectorGeometry(-halfWidth, -60), new VectorGeometry(-halfWidth + 60, -60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
//        this.addSource(new LineSource(new VectorGeometry(-halfWidth + 60, 60), new VectorGeometry(-halfWidth + 60, -60), false, WALL_FIELD_FORMULA).setSize(WALL_SIZE).setYFactor(WALL_Y_FACTOR));
        return this;
    }

    public static PotentialField worldToPotentialField(DynamicWorld world){
        PotentialField field = new PotentialField();
        field.addWalls();
        for(Robot r : world.getRobots()){
            if(r != null && r.type != RobotType.FRIEND_2){
                field.addSource(new PointSource(r.location.clone(), false, FieldFormula.ONE_OVER_X2).setSize(35));
            }
        }

        Robot us = world.getRobot(RobotType.FRIEND_2);
        field.addEnemyDefence();
        Robot friend = world.getRobot(RobotType.FRIEND_1);
        if(friend != null && WorldTools.isPointInFriendDefenceArea(friend.location) && us != null && !WorldTools.isPointInFriendDefenceArea(us.location) ){
            field.addFriendDefence();
        }

        return field;
    }

    public void setObstacles(LinkedList<Obstacle> obstacles) {
        for(Obstacle o : obstacles){
            this.addSource(new PointSource(new VectorGeometry(o.x,o.y), false, FieldFormula.E_TO_MINUS_X).setSize(30));
        }
    }
}
