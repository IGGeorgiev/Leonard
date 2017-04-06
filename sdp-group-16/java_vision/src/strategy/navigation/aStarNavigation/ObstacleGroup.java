package strategy.navigation.aStarNavigation;


import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class ObstacleGroup {
    private LinkedList<AStarObstacle> lines;

    public ObstacleGroup(){
        this.lines = new LinkedList<AStarObstacle>();
    }

    public void addSource(AStarObstacle line){
        this.lines.add(line);
    }

    public void addToField(ObstacleField field){
        for(AStarObstacle s : this.lines){
            field.addAStarObstacle(s);
        }
    }

    @Override
    public String toString(){
        String res = "";
        for(AStarObstacle s : this.lines){
            res = res + s + "\n";
        }
        return res;
    }
}
