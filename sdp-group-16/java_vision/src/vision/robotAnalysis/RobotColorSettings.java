package vision.robotAnalysis;

import vision.colorAnalysis.SDPColor;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class RobotColorSettings {

    /*    DO NOT REWRITE ANYTHING FROM HERE */
    /* */ public static SDPColor FRIEND_COLOR = SDPColor.YELLOW;
    /* */ public static SDPColor FOE_COLOR = SDPColor.BLUE;
    /* */ public static boolean FRIEND_1_IS_GREEN = true;
    /* */ public static boolean FOE_1_IS_GREEN = true;
    /* */ public static boolean ASSUME_YELLOW = true;
    /*    TO HERE WITHOUT REWRITING the MiscellaneousSettings class. */

    public static SDPColor getMainColor(RobotType type){
        if(type == RobotType.FRIEND_1 && FRIEND_1_IS_GREEN) return SDPColor.GREEN;
        if(type == RobotType.FRIEND_2 && !FRIEND_1_IS_GREEN) return SDPColor.GREEN;
        if(type == RobotType.FOE_1 && FOE_1_IS_GREEN) return SDPColor.GREEN;
        if(type == RobotType.FOE_2 && !FOE_1_IS_GREEN) return SDPColor.GREEN;
        return SDPColor.PINK;
    }

    public static SDPColor getTeam(RobotType type){
        if(type == RobotType.FRIEND_1 || type == RobotType.FRIEND_2) return FRIEND_COLOR;
        return FOE_COLOR;
    }

    public static RobotType getRobotType(SDPColor team, SDPColor mainColor){
        StringBuilder builder = new StringBuilder();

        if(team == FRIEND_COLOR){
            builder.append("FRIEND_");
            if((mainColor == SDPColor.GREEN && FRIEND_1_IS_GREEN) || (mainColor == SDPColor.PINK && !FRIEND_1_IS_GREEN)){
                builder.append('1');
            } else {
                builder.append('2');
            }
        } else {
            builder.append("FOE_");
            if((mainColor == SDPColor.GREEN && FOE_1_IS_GREEN) || (mainColor == SDPColor.PINK && !FOE_1_IS_GREEN)){
                builder.append('1');
            } else {
                builder.append('2');
            }
        }
        return RobotType.valueOf(builder.toString());
    }
}
