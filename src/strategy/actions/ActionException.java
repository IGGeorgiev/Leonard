package strategy.actions;
/**
 * Created by Simon Rovder
 */
public class ActionException extends Exception{
    private boolean success;
    private boolean continueOnExit;
    
    public ActionException(boolean success, boolean continueOnExit){
        this.success = success;
        this.continueOnExit = continueOnExit;
    }

    public boolean getContinueOnExit(){ return this.continueOnExit;}
    
    public boolean getSuccess(){
        return this.success;
    }
}
