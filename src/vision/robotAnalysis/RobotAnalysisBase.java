package vision.robotAnalysis;

import vision.DynamicWorld;
import vision.distortion.DistortionListener;
import vision.spotAnalysis.SpotAnalysisBase;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public abstract class RobotAnalysisBase implements DistortionListener {

    private LinkedList<DynamicWorldListener> listeners;

    protected DynamicWorld lastKnownWorld = null;

    public RobotAnalysisBase(){
        this.listeners = new LinkedList<DynamicWorldListener>();
    }

    public void addDynamicWorldListener(DynamicWorldListener listener){
        this.listeners.add(listener);
    }

    protected void informListeners(DynamicWorld world){

        this.lastKnownWorld = world;
        for(DynamicWorldListener listener : this.listeners){
            listener.nextDynamicWorld(world);
        }
    }
}
