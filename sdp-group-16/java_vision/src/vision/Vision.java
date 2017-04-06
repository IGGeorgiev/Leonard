package vision;

import org.opencv.core.Core;
import strategy.Strategy;
import vision.colorAnalysis.ColorCalibration;
import vision.gui.DetectionCalibrationGUI;
import vision.gui.MiscellaneousSettings;
import vision.gui.SDPConsole;
import vision.kalmanFilter.DynamicWorldFilter;
import vision.objectRecognition.ImageManipulationPipeline;
import vision.objectRecognition.detection.DetectionPropertiesManager;
import vision.rawInput.RawInput;
import vision.robotAnalysis.DynamicWorldListener;
import vision.robotAnalysis.RobotAnalysisBase;
import vision.robotAnalysis.RobotPreview;
import vision.robotAnalysis.newRobotAnalysis.NewRobotAnalysis;
import vision.spotAnalysis.SpotAnalysisBase;
import vision.spotAnalysis.approximatedSpotAnalysis.ApproximatedSpotAnalysis;
import vision.spotAnalysis.recursiveSpotAnalysis.RecursiveSpotAnalysis;
import vision.tools.CommandLineParser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

import static vision.objectRecognition.detection.DetectionPropertiesManager.saveValues;
import static vision.settings.SettingsManager.loadSettings;

/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE
 * This is the main Vision class. It creates the entire vision system. Run this file to see the magic. :)
 */

public class Vision extends JFrame implements DynamicWorldListener, ChangeListener {

	private LinkedList<VisionListener> visionListeners;
	private DetectionCalibrationGUI detectionGUI;
	public static SpotAnalysisBase recursiveSpotAnalysis   = new RecursiveSpotAnalysis();
	
	/**
	 * Add a vision listener. The Listener will be notified whenever the
	 * vision system has a new world.
	 * @param visionListener Your class
	 */
	public void addVisionListener(VisionListener visionListener){
		this.visionListeners.add(visionListener);
	}
	
	/**
	 * Vision system constructor. Please please please only call this once, or else it goes haywire.
	 */
	public Vision(String[] args){
		super("Vision");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		DynamicWorldFilter kalmanFilters = new DynamicWorldFilter();
		
		this.visionListeners   = new LinkedList<VisionListener>();
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(this);

//		SpotAnalysisBase approximateSpotAnalysis = new ApproximatedSpotAnalysis();

		detectionGUI = new DetectionCalibrationGUI();
		detectionGUI.hideAll();

		// NOTICE: ORDER IS IMPORTANT FOR FUNCTIONALITY... Sadly...
		// SDP2017NOTE
		// This part builds the vision system pipeline
//		RawInput.addRawInputListener(recursiveSpotAnalysis);
//		RawInput.addRawInputListener(Preview.preview);
		RawInput.addRawInputListener(ImageManipulationPipeline.getInstance());
		recursiveSpotAnalysis.addSpotListener(RobotPreview.preview);
//		RawInput.addRawInputListener(Distortion.distortion);
//		recursiveSpotAnalysis.addSpotListener(Distortion.distortion);
//		DistortionPreview.addDistortionPreviewClickListener(Distortion.distortion);
//		Distortion.addDistortionListener(RobotPreview.preview);

		RobotAnalysisBase robotAnalysis = new NewRobotAnalysis();
		recursiveSpotAnalysis.addSpotListener(robotAnalysis);
//		Distortion.addDistortionListener(robotAnalysis);
		robotAnalysis.addDynamicWorldListener(kalmanFilters);
		kalmanFilters.addFilterListener(RobotPreview.preview);
		kalmanFilters.addFilterListener(this);
		
		
		tabbedPane.addTab("Input Selection", null, RawInput.rawInputMultiplexer, null);

		tabbedPane.addTab("BackgroundSubtraction", null, new DetectionCalibrationGUI(), null);

		tabbedPane.addTab("Color Calibration", null, ColorCalibration.colorCalibration, null);
//		tabbedPane.addTab("Distortion", null, Distortion.distortion, null);
//		tabbedPane.addTab("Robots", null, RobotAnalysis.strategy.robots, null);
		tabbedPane.addTab("Misc Settings", null,  MiscellaneousSettings.miscSettings, null);
		
		SDPConsole.console.setVisible(true);
		
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		this.setSize(640,480);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				terminateVision();
			}
		});

		args = filterArgs(args);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CommandLineParser.parser.newParse(args, this);
		this.setVisible(true);
	}

	/**
	 * Call this function to safely turn off all the Vision stuff.
	 */
	public void terminateVision(){
		saveValues();
		RawInput.rawInputMultiplexer.stopAllInputs();
	}

	public static void main(String[] args){
		new Vision(args);
	}

	// World Transmitter
	private void toWorldSender(DynamicWorld world){
		String[] msg = new String[4];
		try {

//            System.out.println("wtf 11111");
			if (world.getBall() != null) {
				msg[0] = "BALL";
				msg[1] = String.format("%.3f", (world.getBall().location.x));
				msg[2] = String.format("%.3f", (world.getBall().location.y));
//                System.out.println(msg[0] + " " + msg[1] + " " + msg[2]);
				WorldSender.main(msg);
			}
			for (RobotType t : RobotType.values()){
				if (world.getRobot(t) != null){
					msg[0] = t.toString();
					msg[1] = String.format("%.3f", (world.getRobot(t).location.x));
					msg[2] = String.format("%.3f", (world.getRobot(t).location.y));
					msg[3] = String.format("%.3f", (world.getRobot(t).location.direction));
					WorldSender.main(msg);
				}
			}
//            System.out.println("wtf 44444");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextDynamicWorld(DynamicWorld state) {
		new Thread(() -> toWorldSender(state)).start();
		for(VisionListener visionListener : this.visionListeners){
			visionListener.nextWorld(state);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
		if (tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("BackgroundSubtraction")) {
			detectionGUI.showAll();
		} else {
			detectionGUI.hideAll();
		}
	}

	private String[] filterArgs(String[] args) {
		if (args.length != 0 && args[0].length() == 1 && Character.isDigit(args[0].charAt(0)) ) {

			String[] aux_args = new String[args.length - 1];
			System.arraycopy(args, 1, aux_args, 0, args.length - 1);

			// Load detection
			String path = "src/../settings/";
			if (Integer.valueOf(args[0]) == 2) {
				System.out.println("Loading pitch 2 settings");
				DetectionPropertiesManager.loadValues(path + "pitch2.properties");

				try {
					loadSettings(path + "pitch2.conf");
				} catch (Exception ignore) {}
			} else {
				System.out.println("Loading pitch 1 settings");
				DetectionPropertiesManager.loadValues(path + "pitch1.properties");
				try {
					loadSettings(path + "pitch1.conf");
				} catch (Exception ignore) {}
			}
			return aux_args;
		} else {
			System.out.println("No settings pre-loaded");
			return args;
		}
	}
}