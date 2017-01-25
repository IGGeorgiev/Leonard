package vision.colorAnalysis;

import java.awt.Color;
import java.util.HashMap;
/**
 * Created by Simon Rovder
 */
public class SDPColors {
	
	public static final SDPColors sdpColors = new SDPColors();
	
	public static HashMap<SDPColor, SDPColorInstance> colors;
	
	private SDPColors(){
		colors = new HashMap<SDPColor, SDPColorInstance>();
		for(SDPColor c : SDPColor.values()){
			colors.put(c,  new SDPColorInstance(c.toString(), new Color(255,0,0), c));
		}
	}
}
