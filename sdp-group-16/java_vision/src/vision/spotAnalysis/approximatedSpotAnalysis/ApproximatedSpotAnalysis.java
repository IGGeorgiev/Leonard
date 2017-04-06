package vision.spotAnalysis.approximatedSpotAnalysis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.gui.Preview;
import vision.rawInput.RawInputListener;
import vision.spotAnalysis.SpotAnalysisBase;

import static vision.tools.ImageTools.rgbToHsv;

/**
 * Created by Simon Rovder
 *
 * VERY DANGEROUS CLASS! Sometimes fails misserably, this is the predecessor of the RecursiveSpotAnalysis class
 */
public class ApproximatedSpotAnalysis extends SpotAnalysisBase implements RawInputListener{

	private HashMap<SDPColor, RegionFinder> regionFinders;
	protected int[] rgb;
	protected float[] hsv;
	protected int[] changeTracker;
	protected int[] rgbFilter;
	protected float[] hsvFilter;
	private RegionFinder filterer;
	private int haveImage = 5;

	
	public void updateFilter(BufferedImage bi){
		
//		try {
//		    bi = ImageIO.read(new File("~/Pictures/mask.jpg"));
//		    if(bi.getWidth() != Constants.INPUT_WIDTH || bi.getHeight() != Constants.INPUT_HEIGHT){
//		    	SDPConsole.message("The mask you tried to open is not the correct dimensions. The dimensions are supposed to be " + Constants.INPUT_WIDTH + " by " + Constants.INPUT_HEIGHT + "!");
//		    	return;
//		    }
//		} catch (Exception e) {
//			SDPConsole.message("Could not open mask image. Something went wrong. Try JPG and JPEG images of size 640 by 480.");
//			e.printStackTrace();
//		}
//		
//		bi.getRaster().getPixels(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, this.rgbFilter);
//		rgbToHsv(this.rgbFilter, this.hsvFilter);
	}
	
	public ApproximatedSpotAnalysis(){
		super();
		this.regionFinders  = new HashMap<SDPColor, RegionFinder>();
		for(SDPColor c : SDPColor.values()){
			this.regionFinders.put(c, new RegionFinder(c, 1));
		}
		this.rgb       = new int[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
		this.hsv       = new float[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
		this.rgbFilter = new int[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
		this.hsvFilter = new float[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
	}

	@Override
	public void nextFrame(BufferedImage bi, long time){
		if(this.haveImage == 0){
			this.updateFilter(bi);
			this.haveImage--;
		}





		// 15ms SECTION
		Raster raster = bi.getData();
		raster.getPixels(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, this.rgb);
		rgbToHsv(this.rgb, this.hsv);
		// SECTION END



		for(RegionFinder rf : this.regionFinders.values()){
			rf.reset();
		}

		Graphics g = Preview.getImageGraphics();
		if(g != null) g.setColor(Color.WHITE);


		// 8ms SECTION
		for(RegionFinder regionFinder : this.regionFinders.values()){
			for(int i = 0; i < Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT; i++){
				if(isChanged(i)){
					regionFinder.nextPixel(hsv[3*i], hsv[3*i+1], hsv[3*i+2], i%Constants.INPUT_WIDTH, i/Constants.INPUT_WIDTH);
				}
			}
		}
		// SECTION END




		HashMap<SDPColor, ArrayList<Spot>> spots = new HashMap<SDPColor, ArrayList<Spot>>();
		for(SDPColor c : SDPColor.values()){
			spots.put(c, new ArrayList<Spot>());
		}
		Spot spot;
		
		
		for(RegionFinder regionFinder : this.regionFinders.values()){
			for(Region2D potentialSpot : regionFinder.getRegion2Ds()){
				spot = Spot.spotSpotter(this.hsv, potentialSpot, regionFinder.color);
				if(spot != null){
					if(g != null) g.drawRect(potentialSpot.x1, potentialSpot.y1, (potentialSpot.x2 - potentialSpot.x1), (potentialSpot.y2 - potentialSpot.y1));
					spots.get(spot.color).add(spot);
				}
			}
			Collections.sort(spots.get(regionFinder.color));
		}
		
		
		Preview.flushToLabel();
		this.informListeners(spots, time);
	}
	
	private float val;
	
	public boolean isChanged(int i){
		return true;
//		System.out.print(hsv[0]);
//		System.out.print(" " + hsv[1]);
//		System.out.println(" " + hsv[2]);
//		return (
//				(hsv[3*i + 1] - hsvFilter[3*i+1] > 0.2 ||
//				hsv[3*i + 2] - hsvFilter[3*i+2] > 0.2)
//				hsv[3*i + 1] > 0.5 &&
//				hsv[3*i + 2] > 0.5
//				
//				);
	}
}
