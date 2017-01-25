package strategy.robots;

import strategy.controllers.essentials.ActionController;
import strategy.controllers.ControllerInterface;
import strategy.drives.DriveInterface;
import strategy.controllers.essentials.MotionController;
import communication.ports.interfaces.RobotPort;
import vision.RobotType;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE
 *
 * If you want to add a robot to the system, implement this class for it and instantiate it in the Constructor of
 * Strategy.java. You can see the sample Fred.java
 */
public abstract class RobotBase implements RobotInterface{

    public final RobotType robotType;

    protected LinkedList<ControllerInterface> controllers;

    public final ActionController ACTION_CONTROLLER = new ActionController(this);
    public final MotionController MOTION_CONTROLLER = new MotionController(this);

    public final RobotPort port;

    public final DriveInterface drive;


    /**
     * Constructor instantiates basic controllers
     * @param robotType The robot type.
     * @param port The sdpPort that is to be used to contact the robot.
     * @param drive The drive implementation (So the robot can move using the automatic navigation)
     */
    public RobotBase(RobotType robotType, RobotPort port, DriveInterface drive){
        this.drive = drive;
        this.port = port;
        this.robotType = robotType;
        this.controllers = new LinkedList<ControllerInterface>();
        this.controllers.add(this.ACTION_CONTROLLER);
        this.controllers.add(this.MOTION_CONTROLLER);
    }

    @Override
    public void perform() {
        this.performAutomatic();
        this.performManual();
    }

    @Override
    public void performAutomatic() {
        for(ControllerInterface controller : this.controllers){
            if(controller.isActive()) controller.perform();
        }
    }

    @Override
    public void setControllersActive(boolean active){
        for (ControllerInterface c : this.controllers){
            c.setActive(active);
        }
    }
}
