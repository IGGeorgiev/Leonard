package vision.spotAnalysis.recursiveSpotAnalysis;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.gui.Preview;
import vision.spotAnalysis.SpotAnalysisBase;
import vision.spotAnalysis.approximatedSpotAnalysis.RegionFinder;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static vision.tools.ImageTools.rgbToHsv;

/**
 * Created by Simon Rovder
 */
public class RecursiveSpotAnalysis extends SpotAnalysisBase{

    private int[] rgb;
    private float[] hsv;
    private SDPColor[] found;


    public RecursiveSpotAnalysis(){
        super();
        // Have arrays of 4 times the size for the inputs\
        // (for red, green, blue, alpha OR hue, saturation, value, alpha)
        this.rgb   = new int[4* Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
        this.hsv   = new float[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];

        // array to keep track of visited spots
        this.found = new SDPColor[Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
    }

    private int getIndex(int x, int y){
        return y*Constants.INPUT_WIDTH*3 + x*3;
    }

    private void processPixel(int x, int y, SDPColorInstance sdpColorInstance, XYCumulativeAverage average, int maxDepth){
        if(maxDepth <= 0 || x < 0 || x >= Constants.INPUT_WIDTH || y < 0 || y >= Constants.INPUT_HEIGHT) return;
        int i = getIndex(x, y);
        if(this.found[i/3] == sdpColorInstance.sdpColor) return;
        if(sdpColorInstance.isColor(this.hsv[i], this.hsv[i + 1], this.hsv[i + 2])){
            average.addPoint(x, y);
            this.found[i/3] = sdpColorInstance.sdpColor;
            this.processPixel(x-1,y, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x+1,y, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x,y+1, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x,y-1, sdpColorInstance, average, maxDepth - 1);
            Graphics g = Preview.getImageGraphics();
            if(g != null && sdpColorInstance.isVisible()){
                g.setColor(Color.WHITE);
                g.drawRect(x,y,1,1);
            }
        }
    }


    @Override
    public void nextFrame(BufferedImage image, long time) {


        Raster raster = image.getData();

        /*
         * SDP2017NOTE
         * This line right here, right below is the reason our vision system is real time. We fetch the
         * rgb values of the Raster into a preallocated array this.rgb, without allocating more memory.
         * We recycle the memory, so garbage collection is never called.
         */
        raster.getPixels(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, this.rgb);
        rgbToHsv(this.rgb, this.hsv);

        HashMap<SDPColor, ArrayList<Spot>> spots = new HashMap<SDPColor, ArrayList<Spot>>();
        for(SDPColor c : SDPColor.values()){
            spots.put(c, new ArrayList<Spot>());
        }

        XYCumulativeAverage average = new XYCumulativeAverage();
        SDPColorInstance colorInstance;
        for(int i = 0 ; i < Constants.INPUT_HEIGHT * Constants.INPUT_WIDTH; i++){
            this.found[i] = null;
        }
        for(SDPColor color : SDPColor.values()){
            colorInstance = SDPColors.colors.get(color);
            for(int y = 0; y < Constants.INPUT_HEIGHT; y++){
                for(int x = 0; x < Constants.INPUT_WIDTH; x++){
                    this.processPixel(x, y, colorInstance, average, 200);
                    if(average.getCount() > 5){
                        spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                    }
                    average.reset();
                }
            }
            Collections.sort(spots.get(color));
        }
        this.informListeners(spots, time);
        Preview.flushToLabel();

    }
}
