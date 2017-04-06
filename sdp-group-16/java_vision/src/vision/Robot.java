package vision;

import vision.tools.DirectedPoint;

/**
 * Created by Simon Rovder
 */
public class Robot {
    public DirectedPoint location;
    public DirectedPoint velocity;
    public RobotType type;
    public RobotAlias alias;


    public Robot(){

    }

    @Override
    public Robot clone(){
        Robot r = new Robot();
        r.location = this.location.clone();
        r.velocity = this.velocity.clone();
        r.type = this.type;
        return r;
    }
}
