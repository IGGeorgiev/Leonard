package vision;

import vision.gui.SDPConsole;
import vision.tools.DirectedPoint;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE
 * This is the object that gets passed out of the Vision System to all its registered listeners. Read this carefully,
 * it is fairly important.
 */
public class DynamicWorld {
    // This HashMap contains the detected robots.
    private HashMap<RobotType, Robot> robots;
    private HashMap<RobotAlias, Robot> aliases;

    // The location of the ball.
    private Ball ball;

    // If the ball was not found (ball is null), this will be the last known location of it.
    private Ball lastKnownBall;

    // If the ball is not found, this will be set to the robot that is most likely holding it.
    private RobotType probableBallHolder;

    public int robotCount;
    public int robotChangeDelay;

    private final long time;

    public long getTime(){
        return this.time;
    }

    public DynamicWorld(long time){
        this.time = time;
        this.robots = new HashMap<RobotType, Robot>();
        this.aliases = new HashMap<RobotAlias, Robot>();
    }

    public Robot getRobot(RobotType type){
        return this.robots.get(type);
    }

    public Robot getRobot(RobotAlias alias){
        return this.aliases.get(alias);
    }


    public Ball getBall(){
        return this.ball;
    }

    public void setBall(Ball ball){
        this.ball = ball;
    }

    public Ball getLastKnownBall(){
        return this.lastKnownBall;
    }

    public void setLastKnownBall(Ball ball){
        this.lastKnownBall = ball;
    }

    public RobotType getProbableBallHolder(){
        return this.probableBallHolder;
    }

    public void setRobot(Robot r){
        this.robots.put(r.type, r);
        if(r.alias != RobotAlias.UNKNOWN){
            this.aliases.put(r.alias, r);
        }
    }


    public void setProbableBallHolder(RobotType type){
        this.probableBallHolder = type;
    }

    public void printData() {
		DirectedPoint p;
		if(this.ball != null){
			SDPConsole.writeln("BALL at " + this.ball.location.x + " : " + this.ball.location.y);
		}
		for(RobotType rt : this.robots.keySet()){
			p = this.robots.get(rt).location;
			SDPConsole.writeln("ROBOT: " + rt + " at " + p.x + " : " + p.y + " heading: " + p.direction);
		}
		if(this.probableBallHolder != null) SDPConsole.writeln("Probable ball holder: " + this.probableBallHolder.toString());
        if(this.lastKnownBall != null) SDPConsole.writeln("Last Known ball: " + this.lastKnownBall.toString());
	}

    public Collection<Robot> getRobots(){
        return this.robots.values();
    }


}
