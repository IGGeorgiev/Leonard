package strategy.actions;
/**
 * Created by Simon Rovder
 */
public interface ActionInterface {

    /**
     * Call this method when a state change happens. Perform all state change operations here.
     * Don't forget to save the state as the current state when done!
     * @param newState Nes state number
     */
    void enterState(int newState);

    /**
     * Call this method to activate the action.
     * @throws ActionException When the action is done.
     */
    void tok() throws ActionException;

    /**
     * Put state change logic into this method.
     * @throws ActionException When the action is done.
     */
    void tik() throws ActionException;

    /**
     * Method that delays further action execution. (enforced in tik() )
     * @param millis
     */
    void delay(long millis);

    /**
     * Make this return something intuitive, it is for the GUI.
     * @return
     */
    String description();
}
