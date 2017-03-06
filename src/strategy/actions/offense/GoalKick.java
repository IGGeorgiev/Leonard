package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.navigation.Obstacle;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

public class GoalKick extends ActionBase {

    private Ball ball;
    private VectorGeometry emgoal;
    public GoalKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "GoalKick";
    }

    @Override
    public void enterState(int newState) {

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        EnemyGoal enemyGoal = new EnemyGoal();
        if (us == null || ball == null) return;

        VectorGeometry ballVec = new VectorGeometry(ball.location.x, ball.location.y);
        VectorGeometry emgoal = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY());
        VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ballVec, 20);

//        this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle((int) ball.location.x, (int) ball.location.y, 10));
        this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
        this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) kickingPoint.x, (int) kickingPoint.y));
        this.robot.MOTION_CONTROLLER.setTolerance(5);
        ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
        ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
        this.state = 0;

    }

    @Override
    public void tok() throws ActionException {

    }
}
