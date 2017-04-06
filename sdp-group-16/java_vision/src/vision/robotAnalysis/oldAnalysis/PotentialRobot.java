package vision.robotAnalysis.oldAnalysis;

import java.util.LinkedList;

import vision.colorAnalysis.SDPColor;
import vision.RobotType;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;
/**
 * Created by Simon Rovder
 */
public class PotentialRobot {
	
	private LinkedList<Spot> greenSpots;
	private LinkedList<Spot> pinkSpots;
	private Spot baseSpot;
	
	public PotentialRobot(Spot baseSpot){
		this.greenSpots = new LinkedList<Spot>();
		this.pinkSpots  = new LinkedList<Spot>();
		this.baseSpot   = baseSpot;
	}
	
	public double distance(Spot s){
		return VectorGeometry.distance(s.x, s.y, this.baseSpot.x, this.baseSpot.y);
	}
	
	public DetectedRobot newSpot(Spot s){
		
		if(s.color == SDPColor.GREEN){
			this.greenSpots.add(s);
		} else if (s.color == SDPColor.PINK){
			this.pinkSpots.add(s);
		}
		
		double angle;
		LinkedList<Spot> determined = null;
		RobotType robotType = null;
		if(this.greenSpots.size() > 1 && this.pinkSpots.size() == 1){
			determined = this.pinkSpots;
			robotType = this.baseSpot.color == SDPColor.BLUE ? RobotType.FRIEND_1 : RobotType.FOE_1;
		} else if (this.greenSpots.size() == 1 && this.pinkSpots.size() > 1){
			determined = this.greenSpots;
			robotType = this.baseSpot.color == SDPColor.BLUE ? RobotType.FRIEND_2 : RobotType.FOE_2;
		} 
		
		if(determined != null){
			angle = VectorGeometry.angle(determined.get(0).x - this.baseSpot.x, determined.get(0).y - this.baseSpot.y) - 2.53073;
			return new DetectedRobot(robotType, new DirectedPoint((int)this.baseSpot.x, (int)this.baseSpot.y, angle));
		}
		
		return null;
	}
	
//	public static void main(String[] args){
//		PotentialRobot pr = new PotentialRobot(new Spot(20,20,20,SDPColor.BLUE));
//		System.out.println(pr.newSpot(new Spot(22,22,20,SDPColor.GREEN)));
//		System.out.println(pr.newSpot(new Spot(18,22,20,SDPColor.GREEN)));
//		System.out.println(pr.newSpot(new Spot(18,18,20,SDPColor.PINK)));
//	}
}
