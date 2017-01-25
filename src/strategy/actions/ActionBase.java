package strategy.actions;

import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.gui.SDPConsole;

/**
 * Created by Simon Rovder
 */
public abstract class ActionBase implements ActionInterface {
    protected DynamicPoint point; // Because most actions need one
    protected ActionBase action = null; // The sub action of the action

    /**
     * When a subaction finishes and throws the ActionException, these variables decide which state to continue
     * executing the current action in. (enterState() will be called automatically by tik() )
     */
    private int successExit, failureExit;

    /**
     * The current state
     */
    protected int state;

    /**
     * The description returned by description(). If null, returns the class name
     */
    protected String rawDescription = null;

    /**
     * This is the time at which the action should start executing again. (Set automatically by delay() )
     */
    private long delayedUntil = 0;

    /**
     * The robot that is performing this action.
     */
    protected final RobotBase robot;

    public ActionBase(RobotBase robot, DynamicPoint point){
        this.robot = robot;
        this.point = point;
        this.enterState(0);
    }

    public ActionBase(RobotBase robot){
        this.robot = robot;
        this.enterState(0);
    }

    protected void enterAction(ActionBase action, int successExit, int failureExit){
        SDPConsole.writeln("Creating action: " + action.getClass().getName());
        this.action = action;
        this.successExit = successExit;
        this.failureExit = failureExit;
    }


    @Override
    public void tik() throws ActionException{
        // Check the delay
        if(this.delayedUntil > System.currentTimeMillis()) return;

        if(this.action == null){
            // If there is an active subaction, activate that action instead of this one.
            this.tok();
        } else {
            // Recalculate the relevant point
            if(this.point != null) this.point.recalculate();
            try{
                // Activate the current action's tik() method (nothing else to do)
                this.action.tik();
            } catch (ActionException e){

                // If this section is reached, it means the subaction has terminated, so we erase the subaction
                this.action = null;

                // If the subaction is requesting a stop, stop the robot.
                if(!e.getContinueOnExit()) this.robot.port.stop();

                // Enter the appropriate state
                if(e.getSuccess()){
                    this.enterState(this.successExit);
                } else {
                    this.enterState(this.failureExit);
                }
            }
        }
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


    /**
     * Delays the action.
     * @param millis Amount of milliseconds to delay by
     */
    @Override
    public void delay(long millis){
        this.delayedUntil = System.currentTimeMillis() + millis;
    }
}
