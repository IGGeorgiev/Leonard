package vision;

import org.opencv.core.Core;
import vision.colorAnalysis.ColorCalibration;
import vision.gui.DetectionCalibrationGUI;
import vision.gui.MiscellaneousSettings;
import vision.gui.SDPConsole;
import vision.kalmanFilter.DynamicWorldFilter;
import vision.objectRecognition.ImageManipulationPipeline;
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
import java.util.LinkedList;

import static vision.objectRecognition.detection.DetectionPropertiesManager.saveValues;

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
	private DynamicWorldFilter kalmanFilters = new DynamicWorldFilter();
	
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
		
		this.visionListeners   = new LinkedList<VisionListener>();
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(this);

		SpotAnalysisBase approximateSpotAnalysis = new ApproximatedSpotAnalysis();

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
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new Vision(args);
	}

	@Override
	public void nextDynamicWorld(DynamicWorld state) {
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
}