package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

public class ReverseEnemyGoalDirection extends DynamicPointBase {
    private EnemyGoal enemyGoal = new EnemyGoal();

    @Override
    public void recalculate() {
        this.enemyGoal.recalculate();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us != null){
            VectorGeometry enemyGoalDir = VectorGeometry.fromTo(us.location.x, us.location.y, this.enemyGoal.getX(), this.enemyGoal.getY()).multiply(-1);
            enemyGoalDir.plus(us.location);
            this.x = (int)enemyGoalDir.x;
            this.y = (int)enemyGoalDir.y;
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}
