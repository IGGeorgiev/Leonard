package strategy.navigation.aStarNavigation;

import strategy.Strategy;
import strategy.navigation.Obstacle;
import strategy.WorldTools;
import vision.DynamicWorld;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class ObstacleField {

    private LinkedList<AStarObstacle> AStarObstacles;

    public ObstacleField(){
        this.AStarObstacles = new LinkedList<AStarObstacle>();
    }

    public void addAStarObstacle(AStarObstacle o){
        this.AStarObstacles.add(o);
    }

    public void addObstacles(LinkedList<Obstacle> obstacles){
        for(Obstacle o : obstacles) this.addAStarObstacle(new Circle(o.x,o.y,o.radius));
    }

    public boolean isFree(int x, int y, int lastX, int lastY){
        for(AStarObstacle o : this.AStarObstacles){
            if(o.contains(x, y, lastX, lastY)) return false;
        }
        return true;
    }



    public ObstacleField addWalls(){
        int halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
        int halfHeight = vision.constants.Constants.PITCH_HEIGHT/2;
        int wallwidth = 30;
        this.addAStarObstacle(new Rectangle(new VectorGeometry(-halfWidth - 50, halfHeight + 50), new VectorGeometry(-halfWidth + wallwidth, -halfHeight - 50)));
        this.addAStarObstacle(new Rectangle(new VectorGeometry(-halfWidth - 50, halfHeight + 50), new VectorGeometry(halfWidth + 50, halfHeight - wallwidth)));
        this.addAStarObstacle(new Rectangle(new VectorGeometry(-halfWidth - 50, -halfHeight + wallwidth), new VectorGeometry(halfWidth + 50, -halfHeight - 50)));
        this.addAStarObstacle(new Rectangle(new VectorGeometry(halfWidth - wallwidth, halfHeight + 50), new VectorGeometry(halfWidth + 50, -halfHeight - 50)));
        return this;
    }

    public ObstacleField addEnemyDefence(){
        double halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
        this.addAStarObstacle(new Rectangle(new VectorGeometry(halfWidth - 60, 60), new VectorGeometry(halfWidth, -60)));
        return this;
    }

    public ObstacleField addFriendDefence(){
        double halfWidth = vision.constants.Constants.PITCH_WIDTH/2;
        this.addAStarObstacle(new Rectangle(new VectorGeometry(-halfWidth, 60), new VectorGeometry(-halfWidth + 60, -60)));
        return this;
    }

    public static ObstacleField worldToObstacleField(DynamicWorld world){
        ObstacleField field = new ObstacleField();
        field.addWalls();
        for(Robot r : Strategy.world.getRobots()){
            if(r != null && r.type != RobotType.FRIEND_2){
                field.addAStarObstacle(new Circle((int)r.location.x, (int)r.location.y, 30));
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

    public static void main(String [] args){
        ObstacleField f = new ObstacleField();
        f.addAStarObstacle(new Circle(0,0,2));
//        System.out.println(f.isFree(5,5));
//        System.out.println(f.isFree(-2,-2));
//        System.out.println(f.isFree(-1,-1));
//        System.out.println(f.isFree(1,1));
    }
}
