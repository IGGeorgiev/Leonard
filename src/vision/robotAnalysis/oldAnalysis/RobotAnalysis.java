package vision.robotAnalysis.oldAnalysis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import vision.Ball;
import vision.DynamicWorld;
import vision.Robot;
import vision.colorAnalysis.SDPColor;
import vision.constants.Constants;
import vision.distortion.DistortionListener;
import vision.robotAnalysis.RobotAnalysisBase;
import vision.robotAnalysis.RobotPreview;
import vision.RobotType;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.VectorGeometry;
/**
 * Created by Simon Rovder
 */
public class RobotAnalysis extends RobotAnalysisBase {

	public static final RobotAnalysis robots = new RobotAnalysis();
	
	public RobotAnalysis(){
		super();
	}

	private SDPColor[] teamColor = {SDPColor.YELLOW, SDPColor.BLUE};
	private SDPColor[] spotColor = {SDPColor.GREEN, SDPColor.PINK};


	private void tryAddSpot(LinkedList<ProbabilisticRobot> pRobots, Spot spot){
		double minDistance = Constants.PITCH_WIDTH;
		double newDistance;
		ProbabilisticRobot minRobot = null;
		for(ProbabilisticRobot pRobot : pRobots){
			newDistance = pRobot.getDistanceFrom(spot.x, spot.y);
			if(newDistance < minDistance){
				minDistance = newDistance;
				minRobot = pRobot;
			}
		}
		if(minRobot != null && minDistance < 20){
			if(minRobot.getTeam() != null && (spot.color == SDPColor.BLUE || spot.color == SDPColor.YELLOW)){
				return;
			} else {
				minRobot.nextSpot(spot);
			}
		} else {
			minRobot = new ProbabilisticRobot();
			minRobot.nextSpot(spot);
			pRobots.add(minRobot);
		}
	}

	@Override
	public void nextUndistortedSpots(HashMap<SDPColor, ArrayList<Spot>> spots, long time) {
		LinkedList<ProbabilisticRobot> pRobots = new LinkedList<ProbabilisticRobot>();



		for(SDPColor c : this.teamColor){
			for(Spot s : spots.get(c)){
				tryAddSpot(pRobots, s);
			}
		}
		for(SDPColor c : this.spotColor){
			for(Spot s : spots.get(c)){
				tryAddSpot(pRobots, s);
			}
		}
		DynamicWorld world = new DynamicWorld(0);


		double distance;
		Robot lastRobot = null;




		double maxProb;
		double nextProb;
		ProbabilisticRobot probable;

		Robot robot;
		ArrayList<Robot> robotsList = new ArrayList<Robot>();


		for(RobotType type : RobotType.values()){
			maxProb = 0;
			probable = null;
			if(lastKnownWorld != null) lastRobot = lastKnownWorld.getRobot(type);
			for(ProbabilisticRobot pr : pRobots){
				if(lastRobot != null){
					distance = pr.getDistanceFrom(lastRobot.location.x, lastRobot.location.y);
				} else {
					distance = 0;
				}
				nextProb = pr.getProbability(type, distance);
				if(nextProb > maxProb){
					maxProb = nextProb;
					probable = pr;
				}
			}
			if(probable != null){
				pRobots.remove(probable);
				robot = probable.toRobot();
				robot.type = type;
				world.setRobot( robot);
				robotsList.add(robot);
			}
		}


		ArrayList<Spot> ballSpots = spots.get(SDPColor._BALL);
		Spot ballSpot;
		int i = 0;
		int initial = ballSpots.size();
		while(i < ballSpots.size()){
			ballSpot = ballSpots.get(i);
			i++;
			for(Robot p : robotsList){
				if(VectorGeometry.distance(p.location.x, p.location.y, ballSpot.x, ballSpot.y) < 20){
					ballSpots.remove(ballSpot);
					i--;
					break;
				}
			}
		}

		Spot biggestBall;
		if(ballSpots.size() > 0){
			biggestBall = ballSpots.get(0);
			Ball ball = new Ball();
			ball.location = new VectorGeometry(biggestBall.x, biggestBall.y);
			world.setBall(ball);
			RobotPreview.preview.drawRect((int)biggestBall.x, (int)biggestBall.y, 10, 10, Color.RED);
		} else {
			if(lastKnownWorld != null){
				if(lastKnownWorld.getProbableBallHolder() != null){
					world.setProbableBallHolder(lastKnownWorld.getProbableBallHolder());
				} else {
					Ball lastBallLocation = lastKnownWorld.getBall();
					if(lastBallLocation != null){
						for(RobotType type : RobotType.values()){
							Robot p = lastKnownWorld.getRobot(type);
							if(p != null){
								if(VectorGeometry.distance(p.location.x,p.location.y,lastBallLocation.location.x, lastBallLocation.location.y) < 30){
									world.setProbableBallHolder(type);
//									SDPConsole.writeln("Setting probable: " + type.toString());
									break;
								}
							}
						}
					}
				}
			}
		}

//		System.out.println(world.getRobotCount());
//		world.printData();
		this.informListeners(world);
	}
}





