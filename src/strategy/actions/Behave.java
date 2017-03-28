package strategy.actions;

import com.sun.tools.internal.jxc.ap.Const;
import com.sun.tools.javac.code.Attribute;
import strategy.Strategy;
import strategy.actions.offense.GoalKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.offense.ShuntKick;
import strategy.actions.other.GotoBall;
import strategy.points.basicPoints.BallPoint;
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
enum BehaviourEnum {
    DEFEND, SHUNT, GOAL, SAFE, EMPTY
}

/**
 * The main Action class. It basically plays the game.
 */
public class Behave extends StatefulActionBase<BehaviourEnum> {


    public static boolean RESET = true;


    public Behave(RobotBase robot) {
        super(robot, null);
        this.nextState = BehaviourEnum.GOAL; // So once Behave is called, the robot will call GotoBall????
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
        Ball ball = Strategy.world.getBall();
        if (ball == null) {
            this.nextState = BehaviourEnum.DEFEND;
        } else {
            Robot us = Strategy.world.getRobot(this.robot.robotType);
            if (us == null) {
                // TODO: Angry yelling
                // tell friend that we are lost
                return this.nextState;
            }
            VectorGeometry ballVG = new VectorGeometry(ball.location.x, ball.location.y);
            Robot friend = Strategy.world.getRobot(RobotType.FRIEND_1);
            Robot foe1 = Strategy.world.getRobot(RobotType.FOE_1);
            Robot foe2 = Strategy.world.getRobot(RobotType.FOE_2);
            VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);

            // if our friend has the ball
            if (friend.location.distance(ballVG) < 5) {
                // go to a certain point on the pitch and call goal.. or intercept enemy but idk
                System.out.println("Our friend has the ball! nextState =  SAFE");
                this.nextState = BehaviourEnum.SAFE;
                return this.nextState;
            }

            // if enemy has the ball
            if (foe1.location.distance(ballVG) <= 5) {
                System.out.println("foe1 has the ball!");
                if (foe1.location.distance(ourGoal) <= Constants.PITCH_WIDTH/2) {
                    // if enemy is on our side of the pitch, defend
                    System.out.println("foe1 is on our side of the pitch!! nextState = DEFEND");
                    this.nextState = BehaviourEnum.DEFEND;
                } else {
                    // intercept enemy and/or defend goal
                    System.out.println("foe1 is NOT on our side of the pitch! nextState = SAFE");
                    this.nextState = BehaviourEnum.SAFE;
                }
                return this.nextState;
            }

            // if enemy has the ball
            if (foe2.location.distance(ballVG) <= 5) {
                System.out.println("foe2 has the ball!");
                if (foe2.location.distance(ourGoal) <= Constants.PITCH_WIDTH/2) {
                    // if enemy is on our side of the pitch, defend
                    System.out.println("foe2 is on our side of the pitch!! nextState = DEFEND");
                    this.nextState = BehaviourEnum.DEFEND;
                } else {
                    // intercept enemy and/or defend goal
                    System.out.println("foe2 is NOT on our side of the pitch! nextState = SAFE");
                    this.nextState = BehaviourEnum.SAFE;
                }
                return this.nextState;
            }

            // if enemy doesn't have the ball and the ball is too close to the other robots
            if (foe1.location.distance(ballVG) < 20 || foe2.location.distance(ballVG) < 20 || friend.location.distance(ballVG) < 20) {
                this.nextState = BehaviourEnum.SHUNT;
                System.out.println("The ball is too close to the wall! nextState = SHUNT");
                return this.nextState;
            }

            // if enemy doesn't have the ball and the ball is too close to wall
            if (Math.abs(ball.location.x) > Constants.PITCH_WIDTH / 2 - 20 && Math.abs(ball.location.y) > Constants.PITCH_HEIGHT / 2 - 20) {
                this.nextState = BehaviourEnum.SHUNT;
                System.out.println("The ball is too close to the robot! nextState = SHUNT");
                return this.nextState;
            }

            // if the other robots are not too close to the ball
            System.out.println("GOAL GOAL GOAL GOAL GOAL GOAL GOAL you can do this Leonard!!!!!!");
            this.nextState = BehaviourEnum.GOAL;
            return this.nextState;


                // us.location.distance(ourGoal) )
//            Robot us = Strategy.world.getRobot(this.robot.robotType);
//            if (us == null) {
//                // TODO: Angry yelling
//            } else {
//                VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
//                if (us.location.distance(ourGoal) > ball.location.distance(ourGoal)) {
//                    this.nextState = BehaviourEnum.SAFE;
//                } else {
////                    if(Math.abs(ball.location.x) > Constants.PITCH_WIDTH/2 - 20 && Math.abs(ball.location.y) > Constants.PITCH_HEIGHT/2 - 20){
////                        this.nextState = BehaviourEnum.SHUNT;
////                    } else {
//                    boolean canKick = true;
//                    boolean closer = true;
//                    for (Robot r : Strategy.world.getRobots()) {
//                        if (r != null && r.type != RobotType.FRIEND_2 && r.velocity.length() < 1)
//                            canKick = canKick && r.location.distance(ball.location) > 5;
//                            closer = closer && us.location.distance(ball.location)< r.location.distance(ball.location);
//                    }
//                    if (canKick && (this.lastState != BehaviourEnum.DEFEND ||
//                            VectorGeometry.angle(ball.velocity, VectorGeometry.fromTo(ball.location, ourGoal)) > 2)
//                            || closer) {
//                        this.nextState = BehaviourEnum.GOAL;
//                    } else {
//                        this.nextState = BehaviourEnum.DEFEND;
//                    }
////                    }
//                }
//            }
        }
//        return this.nextState;
    }
}
