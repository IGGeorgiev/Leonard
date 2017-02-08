package strategy.controllers.leonard;

import communication.ports.interfaces.GrabberEquipedRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class PropellorController extends ControllerBase {
    private int grabberTracker;

    public PropellorController(RobotBase robot) {
        super(robot);
        this.grabberTracker = 0;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.grabberTracker = 0;
    }

    private void propell(int dir){
        GrabberEquipedRobotPort port = (GrabberEquipedRobotPort) this.robot.port;
        if(dir < 0){
            if(this.grabberTracker < -4) return;
            this.grabberTracker--;
        }
        if(dir > 0){
            if(this.grabberTracker > 4) return;
            this.grabberTracker++;
        }
        if (dir == 0){
            if(this.grabberTracker == 0) return;
            if(this.grabberTracker > 0) this.grabberTracker--;
            else this.grabberTracker++;
        }
        port.grabber(-dir);
    }

    @Override
    public void perform(){
        assert (this.robot.port instanceof GrabberEquipedRobotPort);

        Robot us = Strategy.world.getRobot(this.robot.robotType);
        if(us != null){
            if(this.isActive()){
                boolean danger = false;
                if(Math.abs(us.location.x) > Constants.PITCH_WIDTH/2 - 20 && VectorGeometry.angle(VectorGeometry.fromAngular(us.location.direction, 10, null), new VectorGeometry(1,0)) > 1) danger = true;
                if(Math.abs(us.location.y) > Constants.PITCH_HEIGHT/2 - 20 && VectorGeometry.angle(VectorGeometry.fromAngular(us.location.direction, 10, null), new VectorGeometry(0,1)) > 1) danger = true;
                for(Robot r : Strategy.world.getRobots()){
                    if(r.type != us.type && us.location.distance(r.location) < 30){
                        danger = true;
                    }
                }
                if(danger){
                    this.propell(0);
                } else {
                    VectorGeometry toEnemy = new VectorGeometry(Constants.PITCH_WIDTH/2, 0);
                    VectorGeometry direct = VectorGeometry.fromAngular(us.location.direction, 10, null);
                    int newDir = VectorGeometry.crossProductDirection(toEnemy, direct) ? 1 : -1;
                    this.propell(newDir);
                }
            }
        }
    }

}
