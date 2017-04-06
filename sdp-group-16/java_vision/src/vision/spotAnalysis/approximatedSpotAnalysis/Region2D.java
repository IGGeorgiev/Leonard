package vision.spotAnalysis.approximatedSpotAnalysis;
/**
 * Created by Simon Rovder
 */
public class Region2D extends Region1D{
	public final int y1;
	public final int y2;
	
	public Region2D(int x1, int y1, int x2, int y2){
		super(x1, x2);
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public boolean inRegion(int x, int y){
		return this.x1 <= x && this.x2 >= x && this.y1 <= y && this.y2 >= y;
	}
	
	public boolean containsRegion(Region2D reg){
		return this.x1 <= reg.x1 && this.y1 <= reg.y1 && this.x2 >= reg.x2 && this.y2 >= reg.y2;
	}
}
