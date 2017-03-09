package communication.ports.robotPorts;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.GrabberEquipedRobotPort;
import communication.ports.interfaces.KickerEquipedRobotPort;
import communication.ports.interfaces.RobotPort;

/**
 * Created by Simon Rovder
 */
public class FredRobotPort extends RobotPort implements KickerEquipedRobotPort, GrabberEquipedRobotPort, FourWheelHolonomicRobotPort {

    public FredRobotPort(){
        super("pang");
    }

    @Override
    public void fourWheelHolonomicMotion(double front, double back, double left, double right) {
        this.sdpPort.commandSender("r", (int) front, (int) back, (int) left, (int) right);
    }

    @Override
    public void grabber(int spin) {
        this.sdpPort.commandSender("grab", spin);
    }

    @Override
    public void kicker(int spin) {
        this.sdpPort.commandSender("kick", spin);
    }

}
