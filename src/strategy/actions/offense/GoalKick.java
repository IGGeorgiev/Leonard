package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.navigation.Obstacle;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

public class GoalKick extends ActionBase {

    private BallPoint point = new BallPoint();

    public GoalKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "GoalKick";
    }

    @Override
    public void enterState(int newState) {

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        if (us == null || ball == null) return;

        VectorGeometry ballVec = new VectorGeometry(ball.location.x, ball.location.y);
        VectorGeometry emgoal = new VectorGeometry(250, 0);
        VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ballVec, 20);

        this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle((int) ball.location.x, (int) ball.location.y, 30));
        this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
        this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) kickingPoint.x, (int) kickingPoint.y));
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
        ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
        this.state = 0;
    }

    @Override
    public void tok() throws ActionException {

    }
}
