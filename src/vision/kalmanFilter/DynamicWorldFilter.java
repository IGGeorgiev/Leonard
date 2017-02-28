package vision.kalmanFilter;

import vision.DynamicWorld;
import vision.robotAnalysis.DynamicWorldListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Georgiev (s1410984) on 28/02/17.
 */
public class DynamicWorldFilter implements DynamicWorldListener {

    private List<DynamicWorldListener> listeners = new ArrayList<>();

    @Override
    public void nextDynamicWorld(DynamicWorld state) {

        //TODO

//        state.getRobot(RobotAlias.LEONARD);
//        state.getRobot(RobotType.FRIEND_1);
//        state.getRobot(RobotType.FRIEND_2);


        for (DynamicWorldListener listener : listeners)
            listener.nextDynamicWorld(state);
    }

    public void addFilterListener(DynamicWorldListener listener) {
        this.listeners.add(listener);
    }
}