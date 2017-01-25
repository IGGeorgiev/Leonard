package vision.robotAnalysis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import vision.*;
import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.distortion.DistortionListener;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
/**
 * Created by Simon Rovder
 */
public class RobotPreview extends JFrame implements DistortionListener, DynamicWorldListener {
	
	public final JLabel previewLabel;
	
	public static final RobotPreview preview = new RobotPreview();
	
	private BufferedImage image;
	
	private final int bounds = 10;
	
	private void resetImage(){
		Graphics g = this.image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Constants.PITCH_WIDTH + 2*bounds, Constants.PITCH_HEIGHT + 2*bounds);
		g.setColor(Color.WHITE);
		g.drawRect(bounds, bounds, Constants.PITCH_WIDTH, Constants.PITCH_HEIGHT);
		g.drawLine(bounds + Constants.PITCH_WIDTH/2, Constants.PITCH_HEIGHT + bounds, bounds + Constants.PITCH_WIDTH/2, bounds);
		g.drawLine(bounds, Constants.PITCH_HEIGHT/2 + bounds, Constants.PITCH_WIDTH + bounds, Constants.PITCH_HEIGHT/2 + bounds);
	}
	
	private RobotPreview(){
		super("Robot Preview");
		this.image = new BufferedImage(Constants.PITCH_WIDTH + 2*bounds, Constants.PITCH_HEIGHT + 2*bounds, BufferedImage.TYPE_3BYTE_BGR);
		this.resetImage();
		this.setSize(Constants.PITCH_WIDTH + bounds + 10, Constants.PITCH_HEIGHT + 2*bounds + 40);
		this.setResizable(false);
		this.previewLabel = new JLabel();
		this.getContentPane().add(this.previewLabel);
		this.setVisible(true);
	}

	@Override
	public void nextUndistortedSpots(HashMap<SDPColor, ArrayList<Spot>> spots, long time) {
		this.resetImage();
		Graphics g = this.image.getGraphics();
		for(SDPColor color : spots.keySet()){
			for(Spot spot :spots.get(color)){
				g.setColor(SDPColors.colors.get(color).referenceColor);
				g.drawArc((int)spot.x - 2 + Constants.PITCH_WIDTH/2 + bounds, - (int)spot.y - 2 + Constants.PITCH_HEIGHT/2 + bounds, 4, 4, 0, 360);
			}
		}
		this.previewLabel.getGraphics().drawImage(this.image, 0, 0, null);
	}

	public void drawArc(int x, int y, int radius, Color color){
		Graphics g = this.previewLabel.getGraphics();
		if(g != null){
			g.setColor(color);
			g.drawArc(x - radius/2 + Constants.PITCH_WIDTH/2 + bounds, - y - radius/2 + Constants.PITCH_HEIGHT/2 + bounds, radius, radius, 0, 360);
		}
	}
	
	public void drawRect(int x, int y, int width, int height, Color color){
		Graphics g = this.previewLabel.getGraphics();
		if(g != null){
			g.setColor(color);
			g.drawRect(x + Constants.PITCH_WIDTH/2 + bounds - height/2, - y + Constants.PITCH_HEIGHT/2 + bounds - height/2, width, height);
		}
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, Color color){
		Graphics g = this.previewLabel.getGraphics();
		if(g != null){
			g.setColor(color);
			g.drawLine(x1 + Constants.PITCH_WIDTH/2 + bounds, - y1 + Constants.PITCH_HEIGHT/2 + bounds, x2 + Constants.PITCH_WIDTH/2 + bounds, -y2 + Constants.PITCH_HEIGHT/2 + bounds);
		}
	}

	public void drawString(String string, int x, int y){
		Graphics g = this.previewLabel.getGraphics();
		if(g != null){
			g.setColor(Color.WHITE);
			g.drawString(string, x + Constants.PITCH_WIDTH/2 + bounds, - y + Constants.PITCH_HEIGHT/2 + bounds);
		}
	}

	@Override
	public void nextDynamicWorld(DynamicWorld state) {
		Robot dp;
		Graphics g = this.previewLabel.getGraphics();
		g.setColor(Color.WHITE);
		for(RobotType type : RobotType.values()){

			dp = state.getRobot(type);
			if (dp != null ){
				this.drawRect((int)dp.location.x, (int)dp.location.y, 20, 20, Color.WHITE);
				if(dp.alias == RobotAlias.UNKNOWN) this.drawString(type.toString(), (int)dp.location.x + 10, (int)dp.location.y + 10);
				else this.drawString(dp.alias.toString(), (int)dp.location.x + 10, (int)dp.location.y + 10);
				int x = (int) (Math.cos(dp.location.direction) * 50);
				int y = (int) (Math.sin(dp.location.direction) * 50);
				x = x + (int)dp.location.x;
				y = y + (int)dp.location.y;
				this.drawLine((int)dp.location.x, (int)dp.location.y, x, y, Color.WHITE);
			}
		}
		Ball ball = state.getBall();
		if(ball != null){
			this.drawArc((int)ball.location.x, (int)ball.location.y, 10, Color.RED);
		}
		RobotType prob = state.getProbableBallHolder();
		if(prob != null){
			Robot pr = state.getRobot(prob);
			if(pr != null) this.drawRect((int)pr.location.x, (int)pr.location.y, 30, 30, Color.RED);
		}
	}
}
	
	
	
	
