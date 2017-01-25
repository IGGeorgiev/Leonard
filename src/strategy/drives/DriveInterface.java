package strategy.drives;

import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public interface DriveInterface {

    /**
     * SDP2017NOTE
     * If you wnt your robot to be able to move using out automatic NavigationInterface System, you need to implement
     * this method for it. We have a 4 wheel holonomic one already implemented, you can copy the pattern from
     * there.
     *
     * This method will be called automatically by the system. Just implement it, instantiate it and place the
     * instance into your robot's 'drive' variable.
     *
     * @param port The sdpPort of the robot that is to be moved.
     * @param location The location of the robot as found in the DynamicWorld
     * @param force The force acting on the robot (absolute direction in which it is supposed to move
     * @param rotation Rotational offset from the point the robot is to be facing
     * @param factor The required speed of motion.
     */
    void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor);
}
