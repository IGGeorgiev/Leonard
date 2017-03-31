package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
import strategy.actions.other.Stop;
import strategy.controllers.ControllerBase;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GrabberController extends ControllerBase {

    private boolean grabberIsDown;
    private FredRobotPort robotPort;
    private Timer timer;
    public GrabberController(RobotBase robot) {
        super(robot);
        this.grabberIsDown = true;
        robotPort = (FredRobotPort) robot.port;
        robotPort.grabber(0);

    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.grabberIsDown = true;
        robotPort.grabber(0);
    }

    public void grab(int i, int time) {
        System.out.println("using grabber" + i);
        FredRobotPort portie = (FredRobotPort) this.robot.port;
        portie.grabber(i);
        if(timer != null && timer.isRunning()){
            timer.stop();
        }
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //...Perform a task...
                portie.grabber(0);
            }
        };
        timer = new Timer(time, taskPerformer);
        timer.setRepeats(false);
        timer.start();
        if (i==1) {
            this.grabberIsDown = false;
        } else if(i!=0) {
            this.grabberIsDown = true;
        }
    }

    @Override
    public void perform() {
        assert (this.robot.port instanceof GrabberEquipedRobotPort);
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        Ball ball = Strategy.world.getBall();
        if (us != null) {
            if (this.isActive()) {
                boolean ballIsGrabbable = false;
                double xDiff = Math.abs(ball.location.x - us.location.x);
                double yDiff = Math.abs(ball.location.y - us.location.y);
                double distFromBall = (xDiff * xDiff + yDiff * yDiff);
                //experiment with the constant 10 to see what works best
                if (distFromBall < 100) {
                    ballIsGrabbable = true;
//                    if (grabberIsDown) {
//                        this.grab(1,1000);
//                        grabberIsDown = false;
//                    }
                }
            }
        }
    }

//                double angle = VectorGeometry.angle(0,1,-1,1); // 45 degrees
//                VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction + angle, 10, null);
//                VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, .location);
//                double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//
//                if (grabberIsDown && ballIsGrabbable) {
//                    System.out.println("we are close to the ball");
//                    grab(2);
//                    grabberIsDown = false;
//                } else if (grabberIsDown && this.robot.robotType == Strategy.world.getProbableBallHolder()) {
//                    EnemyGoal enemyGoal = new EnemyGoal();
////                    EnemyGoalRobot us = Strategy.world.getRobot(this.robot.robotType);
//                    this.robot.MOTION_CONTROLLER.setDestination((DynamicPoint) us.location);
//                    this.robot.MOTION_CONTROLLER.setHeading(enemyGoal);
//                    System.out.println("we have the ball");
//
////                    boolean openGoal = true;
////                    for (Robot r : Strategy.world.getRobots()) {
////                        if (r != null && r.type != RobotType.FRIEND_2){
////                            VectorGeometry potentialKick = new VectorGeometry(x, y)
////                        }
////                            openGoal = openGoal && r.location.distance(ball.location) > 10;
////                    }
//
//                    VectorGeometry lower = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() - 20);
//                    VectorGeometry upper = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() + 20);
//                    VectorGeometry kickDirection = VectorGeometry.intersectionWithFiniteLine(us.location, VectorGeometry.fromAngular(us.location.direction, 10, null), lower, upper);
//                    System.out.println("We are aiming in the direction: " + Math.abs(kickDirection.y));
//                    boolean facingGoal = Math.abs(kickDirection.y) <= 20;
//                    if (facingGoal) {
//                        System.out.println("we are facing the goal");
//                        grab(2);
//                        grabberIsDown = false;
//                    }
//                } else {
//                    /** the commented code below is a possible strategy to work the grabber
//                     *  right now it just lowers the grabber if the grabber was up
//                     *  robotPort.grabber(1); lowers the grabber
//                     *  robotPort.grabber(2); raises the grabber**/
//                    if (distFromBall < 4) {
//                        grabberIsDown = true;
//                    }
//                }
//            }
//        }
//    }

        public boolean grabberDown () {
            return this.grabberIsDown;
        }

        public boolean isFacingBall (Robot us){
            /**this is the general idea of how to get this to work.*/
//        VectorGeometry kickDirection = VectorGeometry.intersectionWithFiniteLine(us.location, VectorGeometry.fromAngular(us.location.direction, 10, null), lower, upper);
//        System.out.println("We are aiming in the direction: " + Math.abs(kickDirection.y));
//        boolean facingGoal = Math.abs(kickDirection.y) <= 20;
//        if (facingGoal) {
//            System.out.println("we are facing the goal");
//            grab(2);
//            grabberIsDown = false;
            return false;
        }

    }