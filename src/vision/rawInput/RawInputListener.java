package vision.rawInput;

import java.awt.image.BufferedImage;
/**
 * Created by Simon Rovder
 */
public interface RawInputListener {
	public void nextFrame(BufferedImage image, long time);
}
