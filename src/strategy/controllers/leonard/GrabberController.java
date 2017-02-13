package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import communication.ports.robotPorts.FredRobotPort;
import strategy.Strategy;
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
                double distFromBall = xDiff * yDiff;
                //experiment with the constant 5 to see what works best
                if (distFromBall < 5) {
                    ballIsGrabbable = true;
                }
                if (grabberIsDown && this.robot.robotType == Strategy.world.getProbableBallHolder()) {
                    EnemyGoal enemyGoal = new EnemyGoal();
                    robot.MOTION_CONTROLLER.setDestination(new ConstantPoint((int) us.location.x, (int) us.location.y));
                    robot.MOTION_CONTROLLER.setHeading(enemyGoal);

//                    boolean openGoal = true;
//                    for (Robot r : Strategy.world.getRobots()) {
//                        if (r != null && r.type != RobotType.FRIEND_2){
//                            VectorGeometry potentialKick = new VectorGeometry(x, y)
//                        }
//                            openGoal = openGoal && r.location.distance(ball.location) > 10;
//                    }
                    VectorGeometry lower = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() - 20);
                    VectorGeometry upper = new VectorGeometry(enemyGoal.getX(), enemyGoal.getY() + 20);
                    VectorGeometry kickDirection = VectorGeometry.intersectionWithFiniteLine(us.location, VectorGeometry.fromAngular(us.location.direction, 10, null), lower, upper);
                    if (Math.abs(kickDirection.y) <= 20) {
                        robotPort.grabber(2);
                        grabberIsDown = false;
                    }
                } else if (grabberIsDown && ballIsGrabbable) {
                    robotPort.grabber(2);
                    grabberIsDown = false;
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
                    robotPort.grabber(1);
                    grabberIsDown = true;
//                    }
                }
            }
        }
    }
}