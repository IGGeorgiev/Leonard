package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.robots.RobotBase;
import vision.robotAnalysis.RobotPreview;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Simon Rovder
 */
public class RemoteControl extends ActionBase implements KeyListener {

    private boolean w = false;
    private boolean a = false;
    private boolean s = false;
    private boolean d = false;
    private boolean grabbed = true;

    public RemoteControl(RobotBase robot) {
        super(robot);
        RobotPreview.preview.addKeyListener(this);
    }

    @Override
    public void enterState(int newState) {

    }

    @Override
    public void tok() throws ActionException {
//        if(w && !a && !s && !d){
//            Fred.FRED.differentialDrive(-100,100);
//        } else if(w && a && !s && !d){
//            Fred.FRED.differentialDrive(0,100);
//        } else if(w && !a && !s && d){
//            Fred.FRED.differentialDrive(-100,0);
//        } else if(!w && !a && s && !d){
//            Fred.FRED.differentialDrive(100,-100);
//        } else if(!w && a && s && !d){
//            Fred.FRED.differentialDrive(0,-100);
//        } else if(!w && !a && s && d){
//            Fred.FRED.differentialDrive(100,0);
//        } else if(!w && !a && !s && d){
//            Fred.FRED.differentialDrive(-100,-100);
//        } else if(!w && a && !s && !d){
//            Fred.FRED.differentialDrive(100,100);
//        } else {
//            Fred.FRED.stop();
//        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
//        this.w = e.getKeyChar() == 'w' || this.w;
//        this.a = e.getKeyChar() == 'a' || this.a;
//        this.s = e.getKeyChar() == 's' || this.s;
//        this.d = e.getKeyChar() == 'd' || this.d;
//
//        if(e.getKeyChar() == 'l'){
//            Fred.FRED.grab();
//            grabbed = true;
//            this.delay(500);
//        }
//        if(e.getKeyChar() == 'p' && grabbed){
//            Fred.FRED.ungrab();
//            grabbed = false;
//            this.delay(500);
//        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.w = !(e.getKeyChar() == 'w') && this.w;
        this.a = !(e.getKeyChar() == 'a') && this.a;
        this.s = !(e.getKeyChar() == 's') && this.s;
        this.d = !(e.getKeyChar() == 'd') && this.d;
    }
}
