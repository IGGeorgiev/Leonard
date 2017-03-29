package strategy.actions.other;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.actions.offense.GoalKick;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Simon Rovder
 */
public class GotoBall extends ActionBase {
    private double rotation;

    public GotoBall(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = "GOTO";
    }

    @Override
    public void enterState(int newState) {

        if (newState == 1) {
            System.out.println("moving my ass to that bloody point");
            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(this.point.getX(), this.point.getY(), 20));
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        } else if (newState == 5) {
            //Adding this results in Leonard hesitating less when he goes to the ball.
            //This state is onlt used when we are far enough away from the ball due to lack of rotation speed.
            Ball ball = Strategy.world.getBall();
            EnemyGoal enemyGoal = new EnemyGoal();
            VectorGeometry ballVec = new VectorGeometry(ball.location.x, ball.location.y);
            VectorGeometry emgoal = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY());
            VectorGeometry kickingPoint = VectorGeometry.kickBallLocation(emgoal, ballVec, 20);

            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) kickingPoint.x, (int) kickingPoint.y));
            this.robot.MOTION_CONTROLLER.setTolerance(5);
        } else if (newState == 3) {
            System.out.println("trying to move forward!!!");
            this.robot.MOTION_CONTROLLER.clearObstacles();
            // TODO: should move forward here
//            ((FredRobotPort)this.robot.port).grabber(1);
//            this.robot.ACTION_CONTROLLER.setAction(new Demo(robot, 150, 255, 255, 150));
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(255, -255, -255, 255);
            System.out.println("moving forward now! ");
            Fred fred = (Fred) this.robot;
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //...Perform a task...
                    fred.GRABBER_CONTROLLER.grab(2, 500);
                }
            };
            Timer tm = new Timer(1000, taskPerformer);
            tm.setRepeats(false);
            tm.start();
        } else if (newState == 4) {
            System.out.println("yo fix rotation!" + rotation);
            double constant = 100 * rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
        } else { // state 2

        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        double distFromUsToBall = VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y);
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        VectorGeometry heading = new VectorGeometry(this.point.getX(), this.point.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) {
//            // Go to point
//            if (distFromUsToBall > 10) {
//                //Gets there quicker
//                this.enterState(5);
//            } else {
                this.enterState(1);
//            }
        } else if (this.state == 2) {
            // halt
            Fred fred = (Fred) this.robot;
            fred.GRABBER_CONTROLLER.grab(2, 500);
            //TODO: HI!!!!!
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//            }
            ((Fred) this.robot).GRABBER_CONTROLLER.setActive(false);
            ((Fred) this.robot).KICKER_CONTROLLER.setActive(false);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            throw new ActionException(true, false);
//            this.enterAction(new GoalKick(this.robot), 0, 0);
        }

        if (distFromUsToBall < 5) {
            System.out.println("Starting GoalKick");
            this.enterState(2);
        }

        if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10) {
            System.out.println("too close moving backwards!!!!");
            if (Math.abs(rotation) > 0.4) {
                ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(-255, 255, 0, -0);
            }
        } else if (VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 40) {
            System.out.println("================ hit tolerance!! ===========");

            ((Fred) this.robot).GRABBER_CONTROLLER.grab(1, 2000);

            if (Math.abs(rotation) >= 0.1) {
                this.rotation = rotation;
                this.enterState(4);
                return;
            } else {
                this.enterState(3);
            }
        }
    }
}




