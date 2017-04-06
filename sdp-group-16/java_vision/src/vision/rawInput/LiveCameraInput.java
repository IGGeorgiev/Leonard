package vision.rawInput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import vision.constants.Constants;
import vision.gui.Preview;
import vision.gui.SDPConsole;
/**
 * Created by Simon Rovder
 */
public class LiveCameraInput extends AbstractRawInput implements CaptureCallback, ActionListener{

    private VideoDevice     videoDevice;
    private FrameGrabber    frameGrabber;
	
	private JButton startButton;
	private JButton stopButton;
	
	
	private Video video;
	private JSpinner channel;
	private JTextField port;
	
	private JComboBox<CameraChooser> choice;
    
	private CameraChooser choosers[] = {
			  new CameraChooser("STANDARD_PAL",    (short) V4L4JConstants.STANDARD_PAL),
			  new CameraChooser("STANDARD_WEBCAM", (short) V4L4JConstants.STANDARD_WEBCAM),
			  new CameraChooser("STANDARD_SECAM",  (short) V4L4JConstants.STANDARD_SECAM),
			  new CameraChooser("STANDARD_NTSC",   (short) V4L4JConstants.STANDARD_NTSC)
		  };
	
	private class CameraChooser{
		public final String name;
		public final short value;
		
		public CameraChooser(String name, short value){
			this.name = name;
			this.value = value;
		}
		
		public String toString(){
			return name;
		}
	}
	
	public static final LiveCameraInput liveCameraInput = new LiveCameraInput();
	
	private LiveCameraInput(){
		super();
		this.setLayout(null);
		this.tabName = "Camera";
		
		JLabel lblChannel = new JLabel("Channel:");
		lblChannel.setBounds(10, 11, 150, 14);
		this.add(lblChannel);
		
		JLabel lblVideoType = new JLabel("Video Type:");
		lblVideoType.setBounds(10, 36, 150, 14);
		this.add(lblVideoType);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(10, 61, 150, 14);
		this.add(lblPort);
		
		this.channel = new JSpinner();
		this.channel.setModel(new SpinnerNumberModel(2,0,4,1));
		channel.setBounds(170, 8, 150, 20);
		this.add(channel);
		
		this.choice = new JComboBox<CameraChooser>();
		this.choice.setBounds(170, 36, 150, 20);
		this.add(this.choice);
		for(CameraChooser c : this.choosers){
			this.choice.addItem(c);
		}
		

		this.port = new JTextField();
		this.port.setBounds(170, 61, 150, 20);
		this.add(this.port);
		this.port.setColumns(10);
		this.port.setText("/dev/video0");
		
		this.startButton = new JButton("Start Feed");
		this.startButton.addActionListener(this);
		this.startButton.setBounds(10, 90, 150, 22);
		this.add(this.startButton);
		
		this.stopButton = new JButton("Stop Feed");
		this.stopButton.setBounds(170, 90, 150, 22);
		this.stopButton.addActionListener(this);
		this.add(this.stopButton);
		this.startButton.setEnabled(true);
		this.stopButton.setEnabled(false);
    }

    
    /**
     * this method stops the capture and releases the frame grabber and video device
     */
    public void cleanupCapture() {
            try {
                    frameGrabber.stopCapture();
                    videoDevice.releaseFrameGrabber();
                    videoDevice.release();
            } catch (StateException ex) {
                    // the frame grabber may be already stopped, so we just ignore
                    // any exception and simply continue.
            }

    }
	
	@Override
	public void exceptionReceived(V4L4JException arg0) {
		arg0.printStackTrace();
		cleanupCapture();
	}


	@Override
	public void nextFrame(VideoFrame frame) {
		BufferedImage image = frame.getBufferedImage();
		if(image.getWidth() != Constants.INPUT_WIDTH || image.getHeight() != Constants.INPUT_HEIGHT){
			SDPConsole.message("The image you tried to open is not the correct dimensions. The dimensions are supposed to be " + Constants.INPUT_WIDTH + " by " + Constants.INPUT_HEIGHT + "!\nIf you wish to change the standard dimensions, please do so in the file: vision.constants.Constants", this);
			this.stop();
			return;
		}
		this.listener.nextFrame(image, frame.getCaptureTime());
		frame.recycle();
	}

	@Override
	public void stop() {
		System.out.println("Stopping video feed.");
		if(this.isActive()) this.video.destroyCamera();
		this.startButton.setEnabled(true);
		this.stopButton.setEnabled(false);
		this.setActive(false);
	}

	@Override
	public void start() {
		this.video = new Video(this, this.port.getText(), ((Integer)(this.channel.getValue())), ((CameraChooser)(this.choice.getSelectedItem())).value);
		this.setActive(true);
		this.startButton.setEnabled(false);
		this.stopButton.setEnabled(true);
		Preview.preview.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.startButton){
			this.start();
		} else if (e.getSource() == this.stopButton){
			this.stop();
		}
	}
	
	public void setVideoChannel(int port){
		this.stop();
		this.channel.setValue(port);
	}
}
