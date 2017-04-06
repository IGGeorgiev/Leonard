package vision.kalmanFilter;

import vision.DynamicWorld;
import vision.RobotType;
import vision.robotAnalysis.DynamicWorldListener;
import vision.tools.DirectedPoint;

import java.util.ArrayList;
import java.util.List;

import static vision.constants.Constants.PITCH_HEIGHT;
import static vision.constants.Constants.PITCH_WIDTH;

/**
 * Created by Ivan Georgiev (s1410984) on 28/02/17.
 */
public class DynamicWorldFilter implements DynamicWorldListener {

    private List<DynamicWorldListener> listeners = new ArrayList<>();

    private BallFilter ballFilter = new BallFilter();
    private RobotFilter f1Filter = new RobotFilter(RobotType.FRIEND_1, new DirectedPoint(-PITCH_WIDTH/2, PITCH_HEIGHT/2, 0));
    private RobotFilter f2Filter = new RobotFilter(RobotType.FRIEND_2, new DirectedPoint(-PITCH_WIDTH/2, -PITCH_HEIGHT/2, 0));

    @Override
    public void nextDynamicWorld(DynamicWorld state) {
        ballFilter.perform(state);
        f1Filter.perform(state);
        f2Filter.perform(state);

        informListeners(state);
    }

    private void informListeners(DynamicWorld state) {
        for (DynamicWorldListener listener : listeners)
            listener.nextDynamicWorld(state);
    }

    static double enforceMinMax(double max, double min, double value) {
        return (max < value) ? max : (min > value) ? min : value;
    }

    public void addFilterListener(DynamicWorldListener listener) {
        this.listeners.add(listener);
    }
}