package strategy.robots;

/**
 * Created by Simon Rovder
 */
interface RobotInterface {
    void perform();
    void performAutomatic();
    void performManual();
    void setControllersActive(boolean active);
}
