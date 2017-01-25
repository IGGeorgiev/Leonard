package vision.robotAnalysis.oldAnalysis;

import vision.RobotType;
import vision.tools.DirectedPoint;
/**
 * Created by Simon Rovder
 */
public class DetectedRobot {
	public final RobotType robotType;
	public final DirectedPoint location;
	
	
	public DetectedRobot(RobotType r, DirectedPoint location){
		this.robotType = r;
		this.location = location;
	}
}
