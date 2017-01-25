package strategy.controllers.fred;

import communication.ports.interfaces.PropellerEquipedRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class PropellerController extends ControllerBase {
    private int propellerTracker;

    public PropellerController(RobotBase robot) {
        super(robot);
        this.propellerTracker = 0;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.propellerTracker = 0;
    }

    private void propell(int dir){
        PropellerEquipedRobotPort port = (PropellerEquipedRobotPort) this.robot.port;
        if(dir < 0){
            if(this.propellerTracker < -4) return;
            this.propellerTracker--;
        }
        if(dir > 0){
            if(this.propellerTracker > 4) return;
            this.propellerTracker++;
        }
        if (dir == 0){
            if(this.propellerTracker == 0) return;
            if(this.propellerTracker > 0) this.propellerTracker--;
            else this.propellerTracker++;
        }
        port.propeller(-dir);
    }

    @Override
    public void perform(){
        assert (this.robot.port instanceof PropellerEquipedRobotPort);

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
