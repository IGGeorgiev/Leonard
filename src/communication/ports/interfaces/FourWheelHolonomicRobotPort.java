package communication.ports.interfaces;

/**
 * Created by Simon Rovder
 */
public interface FourWheelHolonomicRobotPort {
    void fourWheelHolonomicMotion(double front, double back, double left, double right);
}
