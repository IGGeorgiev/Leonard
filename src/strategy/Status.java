package strategy;

import vision.Ball;
import vision.DynamicWorld;
import vision.Robot;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class Status {

    public enum Behaviour{
        DEFEND, ATTACK
    }
    public enum BallState{
        ME, FRIEND, THEM, FREE, LOST
    }

    private static Behaviour lastBehaviour = null;
    public static Behaviour fixedBehaviour = null;

    public final Behaviour behaviour;
    public final BallState ballState;

    public final boolean ourDefence;

    public Status(DynamicWorld world) {
        Ball ball = world.getBall();
        RobotType prob = world.getProbableBallHolder();

        if (prob != null) {
            switch (prob) {
                case FRIEND_1:
                    this.ballState = BallState.FRIEND;
                    break;
                case FRIEND_2:
                    this.ballState = BallState.ME;
                    break;
                case FOE_1:
                    this.ballState = BallState.THEM;
                    break;
                case FOE_2:
                    this.ballState = BallState.THEM;
                    break;
                default:
                    this.ballState = BallState.LOST;
                    break;
            }
        } else {
            if (ball != null) {
                this.ballState = BallState.FREE;
            } else {
                this.ballState = BallState.LOST;
            }
        }

        Behaviour chosen = null;
        Robot us = world.getRobot(RobotType.FRIEND_2);
        Robot friend = world.getRobot(RobotType.FRIEND_1);
//        if(ball != null) System.out.println("BALL VELOCITY: " + ball.velocity.length());
        if(fixedBehaviour == null){
            if (us != null) {
                if (ball != null && WorldTools.isPointInFriendDefenceArea(ball.location) && ball.velocity.length() < 0.5) {
                    chosen = Behaviour.ATTACK;
                } else if (friend != null && WorldTools.isPointInFriendDefenceArea(friend.location)) {
                    chosen = Behaviour.ATTACK;
                } else if (prob == RobotType.FRIEND_2) {
                    chosen = Behaviour.ATTACK;
                } else {
                    chosen = Behaviour.DEFEND;
                }
            } else {
                chosen = Behaviour.ATTACK;
            }

            if(chosen != lastBehaviour){
                this.behaviour = chosen;
            } else {
                this.behaviour = lastBehaviour;
            }
        } else {
            this.behaviour = fixedBehaviour;
        }

//        this.behaviour = chosen;
        lastBehaviour = this.behaviour;


        this.ourDefence = friend != null && WorldTools.isPointInFriendDefenceArea(friend.location) && us != null && !WorldTools.isPointInFriendDefenceArea(us.location);

    }
}
