package vision.robotAnalysis.oldAnalysis;


import vision.Robot;
import vision.colorAnalysis.SDPColor;
import vision.robotAnalysis.RobotColorSettings;
import vision.RobotType;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class ProbabilisticRobot {
    private SDPColor team;
    private SpotVectorizer greenSpots;
    private SpotVectorizer pinkSpots;
    private SpotVectorizer centerSpots;

    private static double MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME = 0.6;

    public ProbabilisticRobot(){
        this.team   = null;
        this.greenSpots = new SpotVectorizer(SDPColor.GREEN);
        this.pinkSpots  = new SpotVectorizer(SDPColor.PINK);
        this.centerSpots  = new SpotVectorizer(null);
    }


    public Robot toRobot(){
        Robot r = new Robot();
        r.location = new DirectedPoint((int)this.centerSpots.getVector().x, (int)this.centerSpots.getVector().y, this.getHeading());
        return r;
    }

    public void setTeam(Spot s){
        this.team = s.color;
    }

    public void nextSpot(Spot s){
        if(this.team == null) {
            if (s.color == RobotColorSettings.FRIEND_COLOR|| s.color == RobotColorSettings.FOE_COLOR) {
                this.setTeam(s);
            }
        }
        this.centerSpots.addSpot(s.x, s.y, s.magnitude);
        if(s.color == SDPColor.GREEN){
            this.greenSpots.addSpot(s.x, s.y, s.magnitude);
        } else if (s.color == SDPColor.PINK){
            this.pinkSpots.addSpot(s.x, s.y, s.magnitude);
        }
    }



    public SDPColor getTeam(){
        return this.team;
    }

    public double getDistanceFrom(double x, double y){
        return this.centerSpots.getVector().distance(x,y);
    }

    public double getHeading(){
        VectorGeometry pinkDir  = this.pinkSpots.getRelativeVector(this.centerSpots.getVector());
        VectorGeometry greenDir = this.greenSpots.getRelativeVector(this.centerSpots.getVector());
        boolean morePink = this.pinkSpots.getMagnitudeSum() > this.greenSpots.getMagnitudeSum();
        if(morePink){
            if(this.greenSpots.hasSpots()) return greenDir.angle() - (Math.PI - MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME);
            return pinkDir.angle() + MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME;
        }
        if(this.pinkSpots.hasSpots()) return pinkDir.angle() - (Math.PI - MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME);
        return greenDir.angle() + MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME;
    }

    public double getProbability(RobotType type, double distance){
        if(team != null){
            if(team == RobotColorSettings.FRIEND_COLOR && (type==RobotType.FOE_1    || type==RobotType.FOE_2)) return 0;
            if(team == RobotColorSettings.FOE_COLOR    && (type==RobotType.FRIEND_1 || type==RobotType.FRIEND_2)) return 0;
        }
        if((this.greenSpots.getSpotCount() == 0 || this.pinkSpots.getSpotCount() == 0) && team == null) return 0;
        if(this.greenSpots.getSpotCount() + this.pinkSpots.getSpotCount() < 2 && team == null) return 0;
//        if(distance < 2 && team != null) distance = 0;
//        if(distance < 2 && team == null) distance = 300;
        boolean morePink = this.pinkSpots.getMagnitudeSum() > this.greenSpots.getMagnitudeSum();


        if(morePink){
            if(type==RobotType.FOE_1    && RobotColorSettings.FOE_1_IS_GREEN) return 0;
            if(type==RobotType.FOE_2    && !RobotColorSettings.FOE_1_IS_GREEN) return 0;
            if(type==RobotType.FRIEND_1 && RobotColorSettings.FRIEND_1_IS_GREEN) return 0;
            if(type==RobotType.FRIEND_2 && !RobotColorSettings.FRIEND_1_IS_GREEN) return 0;

        } else{
            if(type==RobotType.FOE_1    && !RobotColorSettings.FOE_1_IS_GREEN) return 0;
            if(type==RobotType.FOE_2    && RobotColorSettings.FOE_1_IS_GREEN) return 0;
            if(type==RobotType.FRIEND_1 && !RobotColorSettings.FRIEND_1_IS_GREEN) return 0;
            if(type==RobotType.FRIEND_2 && RobotColorSettings.FRIEND_1_IS_GREEN) return 0;
        }


//        if(morePink  && (type==RobotType.FOE_1 || type==RobotType.FRIEND_1)) return 0;
//        if(!morePink && (type==RobotType.FOE_2 || type==RobotType.FRIEND_2)) return 0;
        distance = distance < 1 ? 1 : distance;
        if(team == null && this.greenSpots.getMagnitudeSum() == 0 && this.pinkSpots.getMagnitudeSum() == 0) return 0;
        int teamBonus = 0;
        if((team == RobotColorSettings.FRIEND_COLOR) && (type == RobotType.FRIEND_1 || type == RobotType.FRIEND_2)) teamBonus = teamBonus + 40;
        if((team == RobotColorSettings.FOE_COLOR) && (type == RobotType.FOE_1 || type == RobotType.FOE_2)) teamBonus = teamBonus + 40;
        return ((double)(this.pinkSpots.getMagnitudeSum() + this.greenSpots.getMagnitudeSum() + teamBonus))/distance;
    }
}
