package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;

public class GrabberController extends ControllerBase {

    private int isDown;

    public GrabberController(RobotBase robot) {
        super(robot);
        this.isDown = 0;

    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }

    @Override
    public void perform() {
        assert (this.robot.port instanceof GrabberEquipedRobotPort);
        if(this.isActive()){
            robot.port.checkGrabber();
        }
    }
}