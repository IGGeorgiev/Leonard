package vision.spotAnalysis.approximatedSpotAnalysis;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.tools.VectorGeometry;
/**
 * Created by Simon Rovder
 */
public class Spot extends VectorGeometry implements Comparable{
	public int magnitude;
	public SDPColor color;
	
	public Spot(double x, double y, int magnitude, SDPColor color){
		this.x = x;
		this.y = y;
		this.color = color;
		this.magnitude = magnitude;
	}
	
	public static Spot spotSpotter(float[] hsv, Region2D region, SDPColor color){
		int x = 0;
		int y = 0;
		int magnitude = 0;
		SDPColorInstance instance = SDPColors.colors.get(color);
		int offset;
		for(int j = region.y1; j < region.y2; j++){
			for(int i = region.x1; i < region.x2; i++){
				offset = j*Constants.INPUT_WIDTH*3 + i*3;
				if(instance.isColor(hsv[offset], hsv[offset+1], hsv[offset+2])){
					x = x + i;
					y = y + j;
					magnitude++;
				}
			}
		}
		if(magnitude > 3){
			return new Spot(((double)x)/magnitude, ((double)y)/magnitude, magnitude, color);
		}
		return null;
	}

	@Override
	public int compareTo(Object o) {
		return ((Spot)o).magnitude - this.magnitude;
	}
}
