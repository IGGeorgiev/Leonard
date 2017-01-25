package strategy.actions;

import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Simon Rovder
 *
 * This is a special action class. The basic ActionBase class implementations function similarly to finite state
 * machines. This class allows an action to manually terminate it's subaction before the subaction has terminated.
 *
 * This is useful because not all actions necessarily terminate at all (example would be the HoldPosition action)
 *
 * This class uses generics to allow you to write code that calculates and compares states, and only allows subactions
 * to proceed if the two states are the same or equivalent. If they are not, the subaction is terminated and tok()
 * gets called.
 *
 * Note that these states do not interfere with the ActionBase.state variable. So you can still use enterState() here.
 */
public abstract class StatefulActionBase<A> extends ActionBase {
    protected A lastState;
    protected A nextState;

    /**
     * This field keeps track of equivalent states. You may not need to use this, only use it for cases where many
     * states need to perform the same subaction (so as to not spam-create them)
     */
    private LinkedList<HashSet<A>> equivalenceSets;

    public StatefulActionBase(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.equivalenceSets = new LinkedList<>();
    }

    /**
     * You need to define this when extending! This method calculates the current state.
     *
     * @return The current state
     */
    protected abstract A getState();

    /**
     * Makes the two states equivalent.
     * @param a Some State
     * @param b Some other state
     */
    protected void addEquivalence(A a, A b){
        for(HashSet<A> set : this.equivalenceSets){
            if(set.contains(a) || set.contains(b)){
                set.add(a);
                set.add(b);
                return;
            }
        }
        HashSet<A> newSet = new HashSet<A>();
        newSet.add(a);
        newSet.add(b);
        this.equivalenceSets.add(newSet);
    }

    /**
     * Checks if the two states are equivalent.
     * @param a Some state.
     * @param b Some other state.
     * @return
     */
    protected boolean checkEquivalent(A a, A b){
        if(a == b) return true;
        for(HashSet<A> set : this.equivalenceSets){
            if(set.contains(a) && set.contains(b)) return true;
        }
        return false;
    }


    /**
     * Slightly extends the functionality of the ActionBase.tik method to also check for equivalent states.
     * @throws ActionException If this action has completed.
     */
    @Override
    public void tik() throws ActionException {
        A current = this.getState();
        if(checkEquivalent(current, this.lastState)){
            if(this.action != null) super.tik();
            return;
        }
        this.tok();
        this.lastState = this.nextState;
    }

    @Override
    public String description() {
        String description = this.rawDescription;
        if(description == null){
            description = this.getClass().getName();
        }
        if(this.action != null) description = description + this.action.description();
        return description;
    }
}