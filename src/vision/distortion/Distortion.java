package vision.distortion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vision.colorAnalysis.SDPColor;
import vision.constants.Constants;
import vision.rawInput.RawInputListener;
import vision.settings.SaveLoadCapable;
import vision.spotAnalysis.NextSpotsListener;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.Point;
import vision.tools.VectorGeometry;
/**
 * Created by Simon Rovder
 */
public class Distortion extends JPanel implements SaveLoadCapable, RawInputListener, ActionListener, ChangeListener, NextSpotsListener, DistortionPreviewClickListener{
	
	public static final Distortion distortion = new Distortion();
	
	private LinkedList<DistortionListener> distortionListeners;
	
	private BufferedImage previewImage = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
	private BufferedImage savedImage;
	
	
	private JButton topLeftButton;
	private JButton bottomRightButton;
	private boolean calibratingTopLeft = false;
	private Point topLeftPoint;
	private boolean calibratingBottomRight = false;
	private Point bottomRightPoint;
	
	
	
	private JSpinner barrelSpinner;
	private JSpinner rotationSpinner;
	private JSpinner xShiftSpinner;
	private JSpinner yShiftSpinner;
	private JSpinner xTiltSpinner;
	private JSpinner yTiltSpinner;
	private JSpinner zoomSpinner;
	
	
	private double barrel;
	private double rotation;
	private int xShift;
	private int yShift;
	private double xTilt;
	private double yTilt;
	private double zoom;
	
	public static boolean ROTATE_PITCH = true;

	private void updateDistortion(){
		if(this.savedImage != null){
			DistortionPreview.preview.setVisible(true);
			VectorGeometry pg = new VectorGeometry();
			barrel   = ((double)((Integer)this.barrelSpinner.getValue()))/20000000;
			rotation = ((double)((Integer)this.rotationSpinner.getValue()))/300;
			xShift   = (Integer)this.xShiftSpinner.getValue();
			yShift   = (Integer)this.yShiftSpinner.getValue();
			xTilt    = ((double)((Integer)this.xTiltSpinner.getValue()))/30000;
			yTilt    = ((double)((Integer)this.yTiltSpinner.getValue()))/30000;
			zoom     = ((double)((Integer)this.zoomSpinner.getValue()))/10;
//		if(zoom < 0) zoom = -1/zoom;
			for(int y = 0; y < this.savedImage.getHeight(); y++){
				for(int x = 0; x < this.savedImage.getWidth(); x++){
					pg.x = x;
					pg.y = y;
					pg.transpose(-Constants.INPUT_WIDTH/2, -Constants.INPUT_HEIGHT/2);
//				distortPoint(pg);
					undistortPoint(pg);
					pg.transpose(Constants.INPUT_WIDTH/2, Constants.INPUT_HEIGHT/2);


					if(pg.x < this.savedImage.getWidth() && pg.x >= 0 && pg.y < this.savedImage.getHeight() && pg.y >= 0){
						this.previewImage.setRGB((int)pg.x, (int)pg.y, this.savedImage.getRGB(x, y));
					}
				}
			}
			Graphics g = DistortionPreview.preview.previewLabel.getGraphics();
			g.drawImage(this.previewImage, 0, 0, null);
			g.setColor(Color.WHITE);
			g.fillRect(this.topLeftPoint.x - 5, this.topLeftPoint.y - 5, 10, 10);
			g.fillRect(this.bottomRightPoint.x - 5, this.bottomRightPoint.y - 5, 10, 10);
			g.drawRect(this.topLeftPoint.x, this.topLeftPoint.y, this.bottomRightPoint.x - this.topLeftPoint.x, this.bottomRightPoint.y - this.topLeftPoint.y);
		}
	}
	
	public static void addDistortionListener(DistortionListener listener){
		Distortion.distortion.distortionListeners.add(listener);
	}
	
	@Override
	public void nextFrame(BufferedImage image, long time) {
		if(this.savedImage == null){
			this.savedImage = image;
			this.updateDistortion();
		}else{
			this.savedImage = image;
		}
	}
	
//	private void distortPoint(PointGeometry pg){
//		pg.transpose(xShift, yShift);
//		pg.rotate(rotation);
//		pg.tilt3D(xTilt, yTilt);
//		pg.barrelDistort(barrel);
//	}
	
	private void undistortPoint(VectorGeometry pg){
		pg.barrelUndistort(barrel);
		pg.factor(this.zoom);
		pg.tilt3D(-xTilt, -yTilt);
		pg.rotate(-rotation);
		pg.transpose(-xShift, -yShift);
	}
	
	private void normalizePoint(VectorGeometry pg){

		pg.x = (pg.x - this.topLeftPoint.x) * Constants.PITCH_WIDTH  / (this.bottomRightPoint.x - this.topLeftPoint.x) - Constants.PITCH_WIDTH/2;
		pg.y = -((pg.y - this.topLeftPoint.y) * Constants.PITCH_HEIGHT / (this.bottomRightPoint.y - this.topLeftPoint.y) - Constants.PITCH_HEIGHT/2);
	}

	private Distortion(){
		super();

		this.distortionListeners = new LinkedList<DistortionListener>();
		this.setLayout(null);
		this.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {}
			@Override
			public void focusGained(FocusEvent e) {
				DistortionPreview.preview.setVisible(Constants.GUI);
			}
		});
		
		this.topLeftPoint     = new Point(10, 10);
		this.bottomRightPoint = new Point(Constants.INPUT_WIDTH - 10, Constants.INPUT_HEIGHT - 10);
		
		barrelSpinner = new JSpinner();
		barrelSpinner.setBounds(185, 11, 158, 20);
		this.barrelSpinner.setModel(new SpinnerNumberModel(0,-100,100,1));
		this.add(barrelSpinner);
		
		JLabel lblBarrelCorrectionConstant = new JLabel("Barrel Correction Constant:");
		lblBarrelCorrectionConstant.setBounds(10, 14, 158, 14);
		this.add(lblBarrelCorrectionConstant);
		
		JLabel lblRotation = new JLabel("Rotation:");
		lblRotation.setBounds(10, 39, 158, 14);
		this.add(lblRotation);
		
		rotationSpinner = new JSpinner();
		rotationSpinner.setBounds(185, 36, 158, 20);
		this.add(rotationSpinner);
		this.rotationSpinner.setModel(new SpinnerNumberModel(0,-700,700,1));
		

		JLabel lblXShift = new JLabel("X Shift:");
		lblXShift.setBounds(10, 64, 158, 14);
		this.add(lblXShift);
		
		xShiftSpinner = new JSpinner();
		xShiftSpinner.setBounds(185, 61, 158, 20);
		this.add(xShiftSpinner);
		
		JLabel lblYShift = new JLabel("Y Shift:");
		lblYShift.setBounds(10, 92, 158, 14);
		this.add(lblYShift);
		
		yShiftSpinner = new JSpinner();
		yShiftSpinner.setBounds(185, 89, 158, 20);
		this.add(yShiftSpinner);
		
		

		JLabel lblXTilt = new JLabel("X Tilt:");
		lblXTilt.setBounds(10, 120, 158, 14);
		this.add(lblXTilt);
		
		xTiltSpinner = new JSpinner();
		xTiltSpinner.setBounds(185, 120, 158, 20);
		this.add(xTiltSpinner);
		this.xTiltSpinner.setModel(new SpinnerNumberModel(0,-700,700,1));
		
		JLabel lblYTilt = new JLabel("Y Tilt:");
		lblYTilt.setBounds(10, 150, 158, 14);
		this.add(lblYTilt);
		
		yTiltSpinner = new JSpinner();
		yTiltSpinner.setBounds(185, 150, 158, 20);
		this.add(yTiltSpinner);
		this.yTiltSpinner.setModel(new SpinnerNumberModel(0,-700,700,1));


		lblYTilt = new JLabel("Zoom:");
		lblYTilt.setBounds(10, 180, 158, 14);
		this.add(lblYTilt);

		zoomSpinner = new JSpinner();
		zoomSpinner.setBounds(185, 180, 158, 20);
		this.add(zoomSpinner);
		this.zoomSpinner.setModel(new SpinnerNumberModel(0,-700,700,1));
		
		
		JLabel lblCornerCalibration = new JLabel("Corner Calibration:");
		lblCornerCalibration.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCornerCalibration.setBounds(10, 200, 158, 29);
		this.add(lblCornerCalibration);

		topLeftButton = new JButton("TOP LEFT");
		topLeftButton.addActionListener(this);
		topLeftButton.setBounds(10, 240, 148, 23);
		this.add(topLeftButton);
		
		bottomRightButton = new JButton("BOTTOM RIGHT");
		bottomRightButton.addActionListener(this);
		bottomRightButton.setBounds(210, 240, 148, 23);
		this.add(bottomRightButton);


		this.barrelSpinner.addChangeListener(this);
		this.rotationSpinner.addChangeListener(this);
		this.xShiftSpinner.addChangeListener(this);
		this.yShiftSpinner.addChangeListener(this);
		this.xTiltSpinner.addChangeListener(this);
		this.yTiltSpinner.addChangeListener(this);
		this.zoomSpinner.addChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.topLeftButton){
			if(this.calibratingTopLeft){
				this.topLeftButton.setText("TOP LEFT");
			}else{
				this.topLeftButton.setText("Calibrating...");
			}
			this.calibratingTopLeft = !this.calibratingTopLeft;
		}
		if(e.getSource() == this.bottomRightButton){
			if(this.calibratingBottomRight){
				this.bottomRightButton.setText("BOTTOM RIGHT");
			}else{
				this.bottomRightButton.setText("Calibrating...");
			}
			this.calibratingBottomRight = !this.calibratingBottomRight;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(this.savedImage != null) this.updateDistortion();
	}


	@Override
	public void distortionPreviewClickHandler(int x, int y) {
		if(this.calibratingBottomRight){
			this.bottomRightPoint = new Point(x,y);
			this.updateDistortion();
		} else if (this.calibratingTopLeft){
			this.topLeftPoint = new Point(x,y);
			this.updateDistortion();
		}
	}

	@Override
	public void nextSpots(HashMap<SDPColor, ArrayList<Spot>> spots, long time){
		for(SDPColor color : spots.keySet()){
			for(Spot spot : spots.get(color)){
				spot.transpose(-Constants.INPUT_WIDTH/2, -Constants.INPUT_HEIGHT/2);
				this.undistortPoint(spot);
				spot.transpose(Constants.INPUT_WIDTH/2, Constants.INPUT_HEIGHT/2);
				this.normalizePoint(spot);
				if(ROTATE_PITCH){
					spot.multiply(-1);
				}
			}
		}
		for(DistortionListener dl : this.distortionListeners){
			dl.nextUndistortedSpots(spots, time);
		}
	}

	@Override
	public String saveSettings() {
		StringBuilder sb = new StringBuilder();
		sb.append((Integer)this.barrelSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.rotationSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.xShiftSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.yShiftSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.xTiltSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.yTiltSpinner.getValue());
		sb.append(";");
		sb.append((Integer)this.zoomSpinner.getValue());
		sb.append(";");
		sb.append(this.topLeftPoint.x);
		sb.append(":");
		sb.append(this.topLeftPoint.y);
		sb.append(";");
		sb.append(this.bottomRightPoint.x);
		sb.append(":");
		sb.append(this.bottomRightPoint.y);
		this.updateDistortion();
		return sb.toString();
	}

	@Override
	public void loadSettings(String settings) {
		String[] values = settings.split(";");
		this.barrelSpinner.setValue(Integer.parseInt(values[0]));
		this.rotationSpinner.setValue(Integer.parseInt(values[1]));
		this.xShiftSpinner.setValue(Integer.parseInt(values[2]));
		this.yShiftSpinner.setValue(Integer.parseInt(values[3]));
		this.xTiltSpinner.setValue(Integer.parseInt(values[4]));
		this.yTiltSpinner.setValue(Integer.parseInt(values[5]));
		this.zoomSpinner.setValue(Integer.parseInt(values[6]));
		String [] points = values[7].split(":");
		this.topLeftPoint.x = Integer.parseInt(points[0]);
		this.topLeftPoint.y = Integer.parseInt(points[1]);
		points = values[8].split(":");
		this.bottomRightPoint.x = Integer.parseInt(points[0]);
		this.bottomRightPoint.y = Integer.parseInt(points[1]);
	}
}
