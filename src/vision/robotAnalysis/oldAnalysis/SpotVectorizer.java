package vision.robotAnalysis.oldAnalysis;

import vision.colorAnalysis.SDPColor;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class SpotVectorizer {

    private SDPColor color;
    private int spotCount;
    private int magnitudeSum;
    private VectorGeometry vector;
    private VectorGeometry temp;


    public SpotVectorizer(SDPColor color){
        this.color  = color;
        this.vector = new VectorGeometry();
        this.temp   = new VectorGeometry();
        this.spotCount = 0;
        this.magnitudeSum = 0;

    }

    public void addSpot(double x, double y, int magnitude){
        this.vector.x = (x + spotCount*this.vector.x)/(spotCount + 1);
        this.vector.y = (y + spotCount*this.vector.y)/(spotCount + 1);
        this.spotCount++;
        this.magnitudeSum = this.magnitudeSum + magnitude;
    }


    public VectorGeometry getRelativeVector(VectorGeometry center){
        VectorGeometry v = new VectorGeometry();
        vector.copyInto(v);
        return v.transpose(-center.x, -center.y);
    }

    public VectorGeometry getVector(){
        return this.vector;
    }

    public boolean hasSpots(){
        return this.spotCount > 0;
    }

    public int getSpotCount(){
        return this.spotCount;
    }

    public int getMagnitudeSum(){
        return this.magnitudeSum;
    }
}
