package strategy.actions;

import strategy.Strategy;
import strategy.actions.offense.ShuntKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.GotoBall;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import strategy.robots.Snorlax;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.WorldSender;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.io.IOException;

/**
 * Created by Simon Rovder
 */
enum BehaviourEnum {
    DEFEND, SHUNT, GOAL, SAFE, EMPTY
}

/**
 * The main Action class. It basically plays the game.
 */
public class Behave extends StatefulActionBase<BehaviourEnum> {


    public static boolean RESET = true;
    private String[] msg = new String[2];


    public Behave(RobotBase robot) {
        super(robot, null);
    }

    @Override
    public void enterState(int newState) {
        if (newState == 0) {
            this.robot.setControllersActive(true);
        }
        this.state = newState;
    }


    @Override
    public void tok() throws ActionException {

        this.robot.MOTION_CONTROLLER.clearObstacles();
        if (this.robot instanceof Fred) ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
        this.lastState = this.nextState;
        if (this.robot instanceof Snorlax) {
            msg[0] = this.nextState.toString();
//            In case we need to only defend
//            msg[0] = "DEFEND";
            try {
                WorldSender.main(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        switch (this.nextState) {
            case DEFEND:
                this.enterAction(new DefendGoal(this.robot), 0, 0);
                break;
            case GOAL:
                this.enterAction(new GotoBall(this.robot, new BallPoint()), 0, 0);
                break;
            case SHUNT:
                this.enterAction(new ShuntKick(this.robot), 0, 0);
                break;
            case SAFE:
                this.enterAction(new GoToSafeLocation(this.robot), 0, 0);
                break;
        }
    }

    @Override
    protected BehaviourEnum getState() {

//        msg[0] = "DEFEND";
//        try {
//            WorldSender.main(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Ball ball = Strategy.world.getBall();

        if (ball == null) {
            this.nextState = BehaviourEnum.DEFEND;
        } else {
            Robot us = Strategy.world.getRobot(this.robot.robotType);
            if (us == null) {
                // TODO: Angry yelling
            } else {
                VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
                if (us.location.distance(ourGoal) > ball.location.distance(ourGoal)) {
                    this.nextState = BehaviourEnum.SAFE;
                } else {
//                    if(Math.abs(ball.location.x) > Constants.PITCH_WIDTH/2 - 20 && Math.abs(ball.location.y) > Constants.PITCH_HEIGHT/2 - 20){
//                        this.nextState = BehaviourEnum.SHUNT;
//                    } else {
                    boolean canKick = true;
                    boolean closer = true;
                    for (Robot r : Strategy.world.getRobots()) {
                        if (r != null && r.type != RobotType.FRIEND_2 && r.velocity.length() < 1)
                            canKick = canKick && r.location.distance(ball.location) > 5;
                            closer = closer && us.location.distance(ball.location)< r.location.distance(ball.location);
                    }
                    if (canKick && (this.lastState != BehaviourEnum.DEFEND ||
                            VectorGeometry.angle(ball.velocity, VectorGeometry.fromTo(ball.location, ourGoal)) > 2)
                            || closer) {
                        this.nextState = BehaviourEnum.GOAL;
                    } else {
                        this.nextState = BehaviourEnum.DEFEND;
                    }
//                    }
                }
            }
        }
        return this.nextState;
    }
}
