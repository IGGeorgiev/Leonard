package strategy.robots;

import strategy.controllers.fred.KickerController;
import strategy.drives.FourWheelHolonomicDrive;
import communication.ports.robotPorts.FredRobotPort;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class Fred extends RobotBase {

    public final KickerController PROPELLER_CONTROLLER = new KickerController(this);

    public Fred(RobotType robotType){
        super(robotType, new FredRobotPort(), new FourWheelHolonomicDrive());
        this.controllers.add(this.PROPELLER_CONTROLLER);
    }


    @Override
    public void performManual() {

    }
}
