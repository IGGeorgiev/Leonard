package strategy.actions.offense;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.Timer;

/**
 * Created by Simon Rovder
 */
public class KickEnemy extends ActionBase {
    Fred fred = (Fred) this.robot;
    private Timer timer = new Timer();
    private boolean fixed = false;

    public KickEnemy(RobotBase robot) {
        super(robot);
        this.rawDescription = "KickEnemy";
    }

    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setActive(false);
        if (newState == 1) {
            this.fixed = false;
            Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
            double rot = calculateRotate(us);
            while (Math.abs(rot) >= 0.15) {
                us = Strategy.world.getRobot(RobotType.FRIEND_2);
                if (us != null) {
                    rot = calculateRotate(us);
                    double constant = 100 * rot;
                    if (Math.abs(constant) >= 70) {
                        constant = 70;
                        if (rot < 0) constant = -70;
                    }
                    if (Math.abs(constant) <= 40) {
                        constant = 40;
                        if (rot < 0) constant = -40;
                    }

                    System.out.println("rotation: " + rot + " constant: " + constant);
                    ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
                } else break;
            }

            this.robot.port.stop();
            this.fixed = true;
        }

//        if (newState == 1) { // state to rotate to face enemy goal
//            System.out.println("yo fix rotation!");
//            double constant = 0;
//            constant = 100 * Math.abs(this.rotation); // TODO: adjust motor speed and clean up this code
//            if (constant>60) constant = 60;
//            if (this.rotation < 0)  constant = constant * -1;
////            double constant = 50 * rotation; // constant has to be big enough or else the rotation will be too slow
////            if (constant>70) constant = 70;
//            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(constant, constant, constant, constant);
//        } else if (newState == 2) { // kick !
//            this.robot.port.halt();
//            fred.GRABBER_CONTROLLER.setActive(true);
//            fred.GRABBER_CONTROLLER.grab(1, 300);
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    fred.KICKER_CONTROLLER.setActive(true);
//                }
//            };
//            timer.schedule(task, 300);
//        }
        this.state = newState;
    }


    private double calculateRotate(Robot us) {
        EnemyGoal emGoal = new EnemyGoal();
        double angle = VectorGeometry.angle(0, 1, -1, 1); // 45 degrees
        double rotation = 0;
        VectorGeometry heading = new VectorGeometry(emGoal.getX(), emGoal.getY());
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
        System.out.println("rotation = " + rotation);
        return rotation;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (us == null) {
            this.enterState(0);
//            return;
        } else if (this.state == 0) { // go to point
            this.enterState(1);
        }


//            if (Math.abs(this.rotation) <= 0.15) { // rotation is fixed
//                System.out.println("rotation is fixed!! kick now");
//                if (this.state != 2) this.enterState(2);
//            } else {
//                this.enterState(1);
//            }


        if (this.fixed == true) System.out.println("Facing the right direction!");
    }
}
