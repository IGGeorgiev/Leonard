package vision.spotAnalysis.recursiveSpotAnalysis;

/**
 * Created by Simon Rovder
 */
public class XYCumulativeAverage {
    private CumulativeAverage x;
    private CumulativeAverage y;

    public XYCumulativeAverage(){
        this.x = new CumulativeAverage();
        this.y = new CumulativeAverage();
    }

    public void addPoint(double x, double y){
        this.x.add(x);
        this.y.add(y);
    }

    public double getXAverage(){
        return this.x.getAverage();
    }

    public double getYAverage(){
        return this.y.getAverage();
    }

    public boolean hasPoints(){
        return this.x.getCount() > 0;
    }

    public int getCount(){
        return this.x.getCount();
    }

    public void reset(){
        this.x.reset();
        this.y.reset();
    }
}
