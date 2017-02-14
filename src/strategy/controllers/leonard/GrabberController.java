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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GrabberController extends ControllerBase {

    private boolean grabberIsDown;
    private FredRobotPort robotPort;

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

    public void grab(int i) {
        FredRobotPort portie = (FredRobotPort) this.robot.port;
        portie.grabber(i);
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //...Perform a task...
                portie.grabber(0);
            }
        };
        Timer tm = new Timer(280, taskPerformer);
        tm.setRepeats(false);
        tm.start();
    }

    @Override
    public void perform() {
        assert (this.robot.port instanceof GrabberEquipedRobotPort);
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        Ball ball = Strategy.world.getBall();
        if (us != null) {
            if (this.isActive()) {
                boolean ballIsGrabbable = false;
                boolean weHaveBall = false;
                FredRobotPort portie = (FredRobotPort) this.robot.port;
                double xDiff = Math.abs(ball.location.x - us.location.x);
                double yDiff = Math.abs(ball.location.y - us.location.y);
                double distFromBall = xDiff * yDiff;
                //experiment with the constant 10 to see what works best
                if (distFromBall < 15) {
                    ballIsGrabbable = true;
                }
                System.out.println("grabber is down: " + grabberIsDown);


                if (grabberIsDown && distFromBall < 15) {
                    System.out.println("we are close to the ball " + distFromBall);
                    System.out.println(grabberIsDown);

                    portie.grabber(2);
                    grabberIsDown = false;
                } else if (grabberIsDown && weHaveBall && this.robot.robotType == Strategy.world.getProbableBallHolder()) {
//                    EnemyGoal enemyGoal = new EnemyGoal();
//                    this.robot.ACTION_CONTROLLER.setAction(new Stop(this.robot));
//                    this.robot.MOTION_CONTROLLER.setHeading(enemyGoal);

                    System.out.println("we have the ball");
                    System.out.println(grabberIsDown);

//                    boolean openGoal = true;
//                    for (Robot r : Strategy.world.getRobots()) {
//                        if (r != null && r.type != RobotType.FRIEND_2){
//                            VectorGeometry potentialKick = new VectorGeometry(x, y)
//                        }
//                            openGoal = openGoal && r.location.distance(ball.location) > 10;
//                    }
//                    VectorGeometry lower = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() - 20);
//                    VectorGeometry upper = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() + 20);
//                    VectorGeometry kickDirection = VectorGeometry.intersectionWithFiniteLine(us.location, VectorGeometry.fromAngular(us.location.direction, 10, null), lower, upper);
//                    System.out.println("We are aiming in the direction: " + Math.abs(kickDirection.y));
//                    boolean facingGoal = Math.abs(kickDirection.y) <= 20;
//                    if (facingGoal) {
//                        System.out.println("we are facing the goal");
                    portie.grabber(2);
                    grabberIsDown = false;
                    weHaveBall = false;
                    //                    }
                } else {
                    /** the commented code below is a possible strategy to work the grabber
                     *  right now it just lowers the grabber if the grabber was up
                     *  robotPort.grabber(1); lowers the grabber
                     *  robotPort.grabber(2); raises the grabber**/
//                    if(ballIsGrabbable && sensor says ball is not within reach){
//                        robotPort.grabber(0);
//                    }else if(ballIsGrabbable && sensor says ball is within reach){
//                        robotPort.grabber(-1);
//                        Strategy.world.setProbableBallHolder(this.robot.robotType);
//                        grabberIsDown = true;
//                    }else{
//                    if (distFromBall < 8) {

                    portie.grabber(1);
                    weHaveBall = true;
                    grabberIsDown = true;
//                    }
//                    }
                }
            }
        }
    }
}