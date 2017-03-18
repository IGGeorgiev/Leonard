package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Simon Rovder
 */
public class KickEnemy extends ActionBase {
    private double rotation;
    Fred fred = (Fred) this.robot;
    public KickEnemy(RobotBase robot) {
        super(robot);
        this.rawDescription = "KickEnemy";
    }
    @Override
    public void enterState(int newState) {
        if (this.state == 1) { // state to rotate to face enemy goal
            System.out.println("yo fix rotation!");
            double constant = 300 * rotation; // constant has to be big enough or else the rotation will be too slow
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
        } else if (this.state == 2) { // kick !
            fred.GRABBER_CONTROLLER.setActive(true);
            fred.GRABBER_CONTROLLER.grab(1, 3000);
//            Timer timer ;
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    fred.KICKER_CONTROLLER.setActive(true);
//                }
//            }
//            timer.schedule(task, 1000);
        }
    }


    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (us == null) {
            this.enterState(0);
            return;
        } else if (this.state == 0) { // go to point
            this.enterState(1);
        } else if (this.state == 1) {
            EnemyGoal emGoal = new EnemyGoal();
            double angle = VectorGeometry.angle(0,1,-1,1); // 45 degrees
            VectorGeometry heading = new VectorGeometry(emGoal.getX(), emGoal.getY());
            VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
            VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
            this.rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);

            if (Math.abs(this.rotation) <= 0.2) { // rotation is fixed
                System.out.println("rotation is fixed!! kick now");
                this.enterState(2);
            }
        }

    }
}
