package strategy.controllers;

import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public abstract class ControllerBase implements ControllerInterface {

    private boolean active;
    public final RobotBase robot;

    public ControllerBase(RobotBase robot) {
        this.active = true;
        this.robot = robot;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
