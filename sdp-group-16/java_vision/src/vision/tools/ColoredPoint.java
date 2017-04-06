package vision.tools;

import java.awt.Color;
/**
 * Created by Simon Rovder
 */
public class ColoredPoint extends Point{

	public final Color color;

	public ColoredPoint(int x, int y, Color c){
		super(x, y);
		this.color = c;
	}
}
