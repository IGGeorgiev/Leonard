package strategy.actions.other;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Demo extends ActionBase {

    private int count = 0;
    //one is the front right motor
    private int one;
    //two is the back left motor
    private int two;
    //three is the front left motor
    private int three;
    //four is the back right motor
    private int four;

    public Demo(RobotBase robot, int one, int two, int three, int four) {
        super(robot);
        assert(robot instanceof Fred);
        this.rawDescription = " Demo Action";
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setActive(false);
        System.out.println("I am demo");
        if(newState == 0){
            ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(-one,two,three,-four);
        } else {
            ((FourWheelHolonomicRobotPort)this.robot.port).fourWheelHolonomicMotion(one,-two,-three,four);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
//        if(VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10) {
//            this.enterState(2);
//        }
//        if(VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 20) {
//            this.enterState(3);
//        } else {
//            if(this.state == 0){
//                this.enterState(1);
//            }
//        }
//        if(count > 0) throw new ActionException(true, false);
        count++;
        if(this.state == 0) this.enterState(1);
        else this.enterState(0);
        this.delay(2000);
    }
}
