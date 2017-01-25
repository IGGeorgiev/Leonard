package vision.spotAnalysis.recursiveSpotAnalysis;

/**
 * Created by Simon Rovder
 */
public class CumulativeAverage {

    private int count;
    private double average;

    public CumulativeAverage(){
        this.reset();
    }

    public void add(double d){
        this.average = (d + this.count*this.average)/(this.count + 1);
        this.count++;
    }

    public double getAverage(){
        return this.average;
    }

    public int getCount(){
        return this.count;
    }

    public void reset(){
        this.count   = 0;
        this.average = 0;
    }
}
