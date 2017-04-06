package vision;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Ball {
    public VectorGeometry location;
    public VectorGeometry velocity;
    public Ball(){ }

    @Override
    public Ball clone(){
        Ball ball = new Ball();
        ball.location = this.location == null ? null : this.location.clone();
        ball.velocity = this.velocity == null ? null : this.velocity.clone();
        return ball;
    }
}
