package vision.spotAnalysis.approximatedSpotAnalysis;

import java.awt.Graphics;
import java.util.ArrayList;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.gui.Preview;
/**
 * Created by Simon Rovder
 */
public class RegionFinder {
	boolean[] xAxis;
    boolean[] yAxis;
    
    public final SDPColorInstance colorInstance;
    public final SDPColor color;
    public int minPixels;
    private int counter;
    
    public RegionFinder(SDPColor color, int minPixels){
    	this.xAxis = new boolean[Constants.INPUT_WIDTH];
    	this.yAxis = new boolean[Constants.INPUT_HEIGHT];
    	this.color = color;
    	this.colorInstance = SDPColors.colors.get(this.color);
    	this.minPixels = minPixels;
    	this.reset();
    }
    
    public void reset(){
    	counter = 0;
    	for(int i = 0; i < this.xAxis.length; i++){
    		this.xAxis[i] = false;
    	}
    	for(int i = 0; i < this.yAxis.length; i++){
    		this.yAxis[i] = false;
    	}
    }
    
    public void nextPixel(float h, float s, float v, int x, int y){
    	if(this.colorInstance.isColor(h, s, v)){
        	Graphics g = Preview.getImageGraphics();
        	if(g != null && this.colorInstance.isVisible()){
        		g.setColor(this.colorInstance.negatedColor);
        		g.drawLine(x, y, x, y);
        	}
    		counter++;
    		this.xAxis[x] = true;
    		this.yAxis[y] = true;
    	}
    }
    
    public void nextPixel(boolean b, int x, int y){
		this.xAxis[x] = b;
		this.yAxis[y] = b;
    }
    
    public ArrayList<Region2D> getRegion2Ds(){
    	
    	ArrayList<Region2D> list = new ArrayList<Region2D>();
    	
    	for(Region1D yRegion : getAxisRegion1Ds(this.yAxis)){
    		for(Region1D xRegion : getAxisRegion1Ds(this.xAxis)){
    			list.add(new Region2D(xRegion.x1, yRegion.x1, xRegion.x2, yRegion.x2));
    		}
    	}
    	return list;
    }
    
    
    private ArrayList<Region1D> getAxisRegion1Ds(boolean[] bools){
    	ArrayList<Region1D> list = new ArrayList<Region1D>();
    	int start = -1;
    	int index = 0;
    	boolean b = false;
    	while(index < bools.length){
    		if(bools[index] != b){
    			if(b){
    				if(index - start > 2)
    				list.add(new Region1D(start, index));
    				b = false;
    				start = -1;
    			} else {
    				b = true;
    				start = index;
    			}
    		}
    		index++;
    	}
    	if(start != -1){
    		list.add(new Region1D(start, index));
    	}
    	return list;
    }
}
