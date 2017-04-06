package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.xml.bind.ValidationEvent;

public class RotateAroundBall extends DynamicPointBase {

    public RotateAroundBall(){
        super();
    }

    @Override
    public void recalculate() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        if(us != null){
            VectorGeometry location = us.location.clone();
            location.setLength(50, ball.location.x, ball.location.y); // adjust distance between ball and robot
            location.rotateAroundPoint(0.3, new VectorGeometry(ball.location.x, ball.location.y));
            this.x = (int)location.x;
            this.y = (int)location.y;
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}