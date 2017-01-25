package vision.tools;

import vision.constants.Constants;

import java.awt.*;

/**
 * Created by Simon Rovder
 */
public class ImageTools {

    private static float[] dummyHSV = {0,0,0,0};

    public static void rgbToHsv(int[] original, float[] target){
        for(int i = 0; i < Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT; i++){
            Color.RGBtoHSB(original[3*i], original[3*i + 1], original[3*i + 2], dummyHSV);
            target[i*3]     = dummyHSV[0];
            target[i*3 + 1] = dummyHSV[1];
            target[i*3 + 2] = dummyHSV[2];
        }
    }
}
