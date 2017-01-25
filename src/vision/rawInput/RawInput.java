package vision.rawInput;

import vision.constants.Constants;
import vision.gui.Preview;
import vision.gui.SDPConsole;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
/**
 * Created by Simon Rovder
 */
public class RawInput extends JPanel{
	
	private JTabbedPane tabbedPane;
	
	public BufferedImage lastImage;
	
	private AbstractRawInput[] rawInputs = {
		LiveCameraInput.liveCameraInput,
		StaticImage.staticImage
	};
	
	private LinkedList<RawInputListener> imageListeners;
	
	public static final RawInput rawInputMultiplexer = new RawInput(); 
	
	
	private RawInput(){
		super();
		this.setLayout(new BorderLayout(0, 0));
		
		this.tabbedPane     = new JTabbedPane(JTabbedPane.TOP);
		this.imageListeners = new LinkedList<RawInputListener>();
		
		this.add(this.tabbedPane);
		
		for(AbstractRawInput rawInput : this.rawInputs){
			rawInput.setInputListener(this);
			this.tabbedPane.addTab(rawInput.getTabName(), null, rawInput, null);
		}
	}
	
	public static void addRawInputListener(RawInputListener ril){
		RawInput.rawInputMultiplexer.imageListeners.add(ril);
	}
	
	public void nextFrame(BufferedImage image, long time){
		this.lastImage = image;
		for(RawInputListener ril : this.imageListeners){
			ril.nextFrame(image, time);
		}
	}

	public void stopAllInputs(){
		for(RawInputInterface input : this.rawInputs){
			input.stop();
		}
	}

	public void setVideoChannel(int port){
		((LiveCameraInput)(this.rawInputs[0])).setVideoChannel(port);
	}
	
	public void streamVideo(){
		this.rawInputs[0].start();
	}
	
	
}
