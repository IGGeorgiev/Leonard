package vision.robotAnalysis.newRobotAnalysis;

import vision.Ball;
import vision.Robot;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
/**
 * Created by Simon Rovder
 */
public class PatternMatcher {
    public static void patternMatch(ArrayList<Spot> spots, ArrayList<RobotPlate> plates){
        /*
         * SDP2017NOTE
         * This is a brute-force plate finder. Yes, it looks slow, but it turns out it is the
         * fastest way.
         */
        for(int i = 0; i < spots.size(); i++){
            for(int j = i + 1; j < spots.size(); j++){
                for(int k = j + 1; k < spots.size(); k++){
                    if( VectorGeometry.distance(spots.get(i), spots.get(j)) < 15 &&
                        VectorGeometry.distance(spots.get(i), spots.get(k)) < 15 &&
                        VectorGeometry.distance(spots.get(k), spots.get(j)) < 15){
                        plates.add(new RobotPlate(spots.get(i), spots.get(j), spots.get(k)));
                    }
                }
            }
        }
    }

    public static void singularValidate(ArrayList<Spot> spots, ArrayList<RobotPlate> plates){
        for(RobotPlate plate : plates){
            for(Spot s : spots){
                if(plate.validate(s)) break;
            }
        }
    }

    public static void removeInvalid(ArrayList<RobotPlate> plates){
        for(int i = 0; i < plates.size(); i++){
            if(!plates.get(i).isValid()){
                plates.remove(i);
                i--;
            }
        }
    }

    public static void teamAnalysis(ArrayList<RobotPlate> plates, ArrayList<Spot> spots){
        for(RobotPlate plate : plates){
            for(Spot s : spots){
                plate.tryAddTeam(s);
            }
        }
    }

    public static boolean isBotPart(ArrayList<RobotPlate> plates, Spot s){
        for(RobotPlate plate : plates){
            if(plate.isBotPart(s)) return true;
        }
        return false;
    }

//    public static boolean posessesBall(Robot robot, Ball ball){
//        VectorGeometry botToBall = ball.location.copyInto(new VectorGeometry()).minus(robot.location);
//        return (botToBall.length() < 20 && VectorGeometry.angle(VectorGeometry.fromAngular(robot.location.direction, 10, null), botToBall) < 0.4);
//    }
}
