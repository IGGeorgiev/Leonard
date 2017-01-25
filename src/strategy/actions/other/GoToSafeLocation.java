package strategy.actions.other;

import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.navigation.Obstacle;
import strategy.points.basicPoints.ConstantPoint;
import communication.ports.robotPorts.FredRobotPort;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class GoToSafeLocation extends ActionBase {
    public GoToSafeLocation(RobotBase robot) {
        super(robot);
        this.rawDescription = " Go To safe location";
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            if(this.robot instanceof Fred) {
                ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(false);
                ((FredRobotPort)this.robot.port).propeller(0);
                ((FredRobotPort)this.robot.port).propeller(0);
                ((FredRobotPort)this.robot.port).propeller(0);
            }


            Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
            Ball ball = Strategy.world.getBall();
            if(us == null || ball == null) return;

            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle((int)ball.location.x, (int)ball.location.y, 30));
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint(-Constants.PITCH_WIDTH/2, 0));
            this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        }
    }

    @Override
    public void tok() throws ActionException {
        if(safe()){
            this.robot.MOTION_CONTROLLER.clearObstacles();
            throw new ActionException(true, false);
        }

    }

    public static boolean safe(){
        Robot us  = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getLastKnownBall();
        if(us == null || ball == null) return false;
        VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH/2, 0);
        return us.location.distance(ourGoal) < ball.location.distance(ourGoal);
    }
}
