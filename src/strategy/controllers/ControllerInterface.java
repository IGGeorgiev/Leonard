package strategy.controllers;

/**
 * Created by Simon Rovder
 */
public interface ControllerInterface {
    void perform();
    boolean isActive();
    void setActive(boolean active);
}
