package strategy.controllers.leonard;


import communication.ports.interfaces.GrabberEquipedRobotPort;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;

public class GrabberController extends ControllerBase {

    public GrabberController(RobotBase robot) {
        super(robot);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }

    @Override
    public void perform() {
        assert (this.robot.port instanceof GrabberEquipedRobotPort);

    }
}