package strategy.controllers.essentials;

import strategy.actions.ActionException;
import strategy.actions.ActionInterface;
import strategy.actions.other.*;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import strategy.GUI;
import vision.tools.DirectedPoint;

/**
 * Created by Simon Rovder
 */
public class ActionController extends ControllerBase {
    public DirectedPoint location;
    public ActionInterface action;

    public ActionController(RobotBase robot){
        super(robot);
        this.location = new DirectedPoint(0, 0, 0);

    }

    public void setLocation(DirectedPoint location){
        if(location != null) this.location = location;
    }

    public void setAction(ActionInterface action){
        if(action != null){
            GUI.gui.action.setText(action.getClass().getName());
        }

        this.action  = action;
    }

    public void commandWait(){
        this.action = new Waiting(this.robot);
    }

    public void commandContemplate(){
        this.action = new Contemplating(null);
    }

    public void perform(){
        if(this.action == null) return;
        try {
            this.action.tik();
        } catch (ActionException e) {
            if(e.getSuccess()){
                if(!e.getContinueOnExit()) this.robot.port.stop();
                this.commandWait();
            }
            else{
                if(!e.getContinueOnExit()) this.robot.port.stop();
                this.commandContemplate();
            }
        }
    }

    public void printDescription(){
        System.out.println(this.robot.robotType.toString() + this.action.description());
    }
}
