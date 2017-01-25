package vision.colorAnalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.beans.editors.ColorEditor;

import vision.gui.Preview;
import vision.gui.PreviewSelectionListener;
import vision.settings.SaveLoadCapable;
import vision.tools.ColoredPoint;


/**
 * Created by Simon Rovder
 */
public class SDPColorInstance extends JFrame implements ActionListener, ChangeListener, PreviewSelectionListener, SaveLoadCapable {

	private JTextField minHueTextField;
	private JTextField maxHueTextField;
	private JTextField minSaturationTextField;
	private JTextField maxSaturationTextField;
	private JTextField minBrightnessTextField;
	private JTextField maxBrightnessTextField;
		
	private float maxHue        = 0;
	private float minHue        = 0;
	private float maxSaturation = 1;
	private float minSaturation = 1;
	private float maxBrightness = 1;
	private float minBrightness = 1;

	private JSlider minHueSlider;
	private JSlider maxHueSlider;
	private JSlider minSaturationSlider;
	private JSlider maxSaturationSlider;
	private JSlider minBrightnessSlider;
	private JSlider maxBrightnessSlider;
	
	private Panel minHuePanel;
	private Panel maxHuePanel;
	private Panel minSaturationPanel;
	private Panel maxSaturationPanel;
	private Panel minBrightnessPanel;
	private Panel maxBrightnessPanel;
	public final SDPColor sdpColor;
	
	public final String name;
	
	private boolean respondToSliderChange;
	private boolean calibrating;
	private JButton done;
	private JButton calibrate;
    public Color referenceColor;
    public Color negatedColor;
	
    
	public boolean isColor(float h, float s, float v){
		if(this.maxHue > 1 && this.minHue > h){
			h++;
		}
		return 
				this.minHue <= h &&
				this.maxHue >= h &&
				this.minSaturation <= s &&
				this.maxSaturation >= s &&
				this.minBrightness <= v &&
				this.maxBrightness >= v;
	}
	
	
	
	public SDPColorInstance(String name, Color referenceColor, SDPColor sdpColor){
		super();
		this.name = name;
		this.setupGUI();
		this.referenceColor = referenceColor;
		this.respondToSliderChange = true;
		this.calibrating = false;
		this.sdpColor = sdpColor;
		Preview.addSelectionListener(this);
	}
	
	private void setupGUI(){
		setTitle(this.name);
		setSize(640,480);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		minHueSlider = new JSlider();
		minHueSlider.setBounds(183, 56, 200, 14);
		panel.add(minHueSlider);
		
		minHuePanel = new Panel();
		minHuePanel.setBounds(140, 56, 37, 14);
		panel.add(minHuePanel);
		
		JLabel lblHue = new JLabel("Min Hue:");
		lblHue.setBounds(10, 56, 120, 14);
		panel.add(lblHue);
		
		minSaturationSlider = new JSlider();
		minSaturationSlider.setBounds(183, 137, 200, 14);
		panel.add(minSaturationSlider);
		
		minSaturationPanel = new Panel();
		minSaturationPanel.setBounds(140, 137, 37, 14);
		panel.add(minSaturationPanel);
		
		JLabel lblMinSaturation = new JLabel("Min Saturation:");
		lblMinSaturation.setBounds(10, 137, 120, 14);
		panel.add(lblMinSaturation);
		
		maxSaturationSlider = new JSlider();
		maxSaturationSlider.setBounds(183, 176, 200, 14);
		panel.add(maxSaturationSlider);
		
		maxSaturationPanel = new Panel();
		maxSaturationPanel.setBounds(140, 176, 37, 14);
		panel.add(maxSaturationPanel);
		
		JLabel lblMaxSaturation = new JLabel("Max Saturation:");
		lblMaxSaturation.setBounds(10, 176, 120, 14);
		panel.add(lblMaxSaturation);
		
		minBrightnessSlider = new JSlider();
		minBrightnessSlider.setBounds(183, 215, 200, 14);
		panel.add(minBrightnessSlider);
		
		minBrightnessPanel = new Panel();
		minBrightnessPanel.setBounds(140, 215, 37, 14);
		panel.add(minBrightnessPanel);
		
		JLabel lblMinValue = new JLabel("Min Brightness:");
		lblMinValue.setBounds(10, 215, 120, 14);
		panel.add(lblMinValue);
		
		maxBrightnessSlider = new JSlider();
		maxBrightnessSlider.setBounds(183, 254, 200, 14);
		panel.add(maxBrightnessSlider);
		
		maxBrightnessPanel = new Panel();
		maxBrightnessPanel.setBounds(140, 254, 37, 14);
		panel.add(maxBrightnessPanel);
		
		JLabel lblMaxValue = new JLabel("Max Brightness:");
		lblMaxValue.setBounds(10, 254, 120, 14);
		panel.add(lblMaxValue);
		
		minHueTextField = new JTextField();
		minHueTextField.setEditable(false);
		minHueTextField.setBounds(389, 39, 86, 26);
		panel.add(minHueTextField);
		minHueTextField.setColumns(10);
		
		minSaturationTextField = new JTextField();
		minSaturationTextField.setEditable(false);
		minSaturationTextField.setColumns(10);
		minSaturationTextField.setBounds(389, 123, 86, 26);
		panel.add(minSaturationTextField);
		
		maxSaturationTextField = new JTextField();
		maxSaturationTextField.setEditable(false);
		maxSaturationTextField.setColumns(10);
		maxSaturationTextField.setBounds(389, 162, 86, 26);
		panel.add(maxSaturationTextField);
		
		minBrightnessTextField = new JTextField();
		minBrightnessTextField.setEditable(false);
		minBrightnessTextField.setColumns(10);
		minBrightnessTextField.setBounds(389, 201, 86, 26);
		panel.add(minBrightnessTextField);
		
		maxBrightnessTextField = new JTextField();
		maxBrightnessTextField.setEditable(false);
		maxBrightnessTextField.setColumns(10);
		maxBrightnessTextField.setBounds(389, 240, 86, 26);
		panel.add(maxBrightnessTextField);
		
		JLabel lblEditBallColor = new JLabel(this.name);
		lblEditBallColor.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEditBallColor.setBounds(10, 11, 465, 26);
		panel.add(lblEditBallColor);
		
		this.done = new JButton("Done");
		done.setBounds(60, 314, 150, 23);
		panel.add(done);
		done.addActionListener(this);
		

		this.calibrate = new JButton("Calibrate");
		this.calibrate.setBounds(10, 350, 250, 23);
		panel.add(this.calibrate);
		this.calibrate.addActionListener(this);
		
		JLabel lblMaxHue = new JLabel("Max Hue:");
		lblMaxHue.setBounds(10, 98, 120, 14);
		panel.add(lblMaxHue);
		
		maxHuePanel = new Panel();
		maxHuePanel.setBounds(140, 98, 37, 14);
		panel.add(maxHuePanel);
		
		maxHueSlider = new JSlider();
		maxHueSlider.setBounds(183, 98, 200, 14);
		panel.add(maxHueSlider);
		
		maxHueTextField = new JTextField();
		maxHueTextField.setEditable(false);
		maxHueTextField.setColumns(10);
		maxHueTextField.setBounds(389, 81, 86, 26);
		panel.add(maxHueTextField);
		this.minHueSlider.setMaximum(100);
		this.minHueSlider.setMinimum(0);
		this.maxHueSlider.setMaximum(100);
		this.maxHueSlider.setMinimum(0);
		this.minSaturationSlider.setMaximum(100);
		this.minSaturationSlider.setMinimum(0);
		this.maxSaturationSlider.setMaximum(100);
		this.maxSaturationSlider.setMinimum(0);
		this.minBrightnessSlider.setMaximum(100);
		this.minBrightnessSlider.setMinimum(0);
		this.maxBrightnessSlider.setMaximum(100);
		this.maxBrightnessSlider.setMinimum(0);
		this.minHueSlider.addChangeListener(this);
		this.maxHueSlider.addChangeListener(this);
		this.minSaturationSlider.addChangeListener(this);
		this.maxSaturationSlider.addChangeListener(this);
		this.minBrightnessSlider.addChangeListener(this);
		this.maxBrightnessSlider.addChangeListener(this);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.recalculateSliders();
		this.myRepaint();
		this.setVisible(false);
	}
	
	private void refreshColors(){
		this.referenceColor = new Color(Color.HSBtoRGB((this.minHue + this.maxHue)/2, (this.maxSaturation + this.minSaturation)/2, (this.minBrightness + this.maxBrightness)/2));
		this.negatedColor = new Color(255 - this.referenceColor.getRed(), 255 - this.referenceColor.getGreen(), 255 - this.referenceColor.getBlue());
	}
	
	private void recalculateValues(){
		this.minHue        = ((float)this.minHueSlider.getValue())/100;
		this.maxHue        = ((float)this.maxHueSlider.getValue())/100;
		if(this.minHue > this.maxHue){
			this.maxHue = this.maxHue+1;
		}
		this.maxSaturation = ((float)this.maxSaturationSlider.getValue())/100;
		this.minSaturation = ((float)this.minSaturationSlider.getValue())/100;
		this.maxBrightness = ((float)this.maxBrightnessSlider.getValue())/100;
		this.minBrightness = ((float)this.minBrightnessSlider.getValue())/100;
	}
	
	private void recalculateSliders(){
		this.minHueSlider.setValue((int)(this.minHue*100));
		this.maxHueSlider.setValue((int)(this.maxHue*100));
		this.maxSaturationSlider.setValue((int)(this.maxSaturation*100));
		this.minSaturationSlider.setValue((int)(this.minSaturation*100));
		this.maxBrightnessSlider.setValue((int)(this.maxBrightness*100));
		this.minBrightnessSlider.setValue((int)(this.minBrightness*100));
	}
	
	public void myRepaint(){
		this.minHueTextField.setText("" + this.minHue);
		this.maxHueTextField.setText("" + this.maxHue);
		this.maxSaturationTextField.setText("" + this.maxSaturation);
		this.minSaturationTextField.setText("" + this.minSaturation);
		this.maxBrightnessTextField.setText("" + this.maxBrightness);
		this.minBrightnessTextField.setText("" + this.minBrightness);
		this.minHuePanel.setBackground(Color.getHSBColor(this.minHue, (this.minSaturation + this.maxSaturation)/2, (this.minBrightness + this.maxBrightness)/2));
		this.maxHuePanel.setBackground(Color.getHSBColor(this.maxHue, (this.minSaturation + this.maxSaturation)/2, (this.minBrightness + this.maxBrightness)/2));
		this.maxSaturationPanel.setBackground(Color.getHSBColor((this.minHue + this.maxHue)/2, this.maxSaturation, (this.minBrightness + this.maxBrightness)/2));
		this.minSaturationPanel.setBackground(Color.getHSBColor((this.minHue + this.maxHue)/2, this.minSaturation, (this.minBrightness + this.maxBrightness)/2));
		this.maxBrightnessPanel.setBackground(Color.getHSBColor((this.minHue + this.maxHue)/2, (this.maxSaturation + this.maxSaturation)/2, this.maxBrightness));
		this.minBrightnessPanel.setBackground(Color.getHSBColor((this.minHue + this.maxHue)/2, (this.minSaturation + this.maxSaturation)/2, this.minBrightness));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.calibrate){
			if(this.calibrating){
				this.done.setEnabled(true);
				this.calibrate.setText("Calibrate");
			} else {
				this.done.setEnabled(false);
				this.calibrate.setText("Calibrating.. Click to end.");
				Preview.preview.transferFocus();
			}
			this.calibrating = !this.calibrating;
		}else{
			this.setVisible(false);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(this.respondToSliderChange){
			this.recalculateValues();
			this.refreshColors();
			this.myRepaint();	
		}
	}



	@Override
	public void previewClickHandler(ColoredPoint coloredPoint) {
		if(this.calibrating && this.isVisible()){
			float[] f = Color.RGBtoHSB(coloredPoint.color.getRed(), coloredPoint.color.getGreen(), coloredPoint.color.getBlue(), null);
			this.maxHue        = (float) (f[0] + 0.05);
			if(this.maxHue > 1){
				this.maxHue = this.maxHue - 1;
			}
			this.minHue        = (float) (f[0] - 0.05);
			if(this.minHue < 0){
				this.minHue = this.minHue + 1;
			}
			if(this.maxHue < this.minHue){
				this.maxHue = this.maxHue + 1;
			}
			this.maxSaturation = 1;
			this.minSaturation = (float) (f[1]-0.05);
			this.maxBrightness = 1;
			this.minBrightness = (float) (f[2]-0.05);
			this.respondToSliderChange = false;
			this.recalculateSliders();
			this.respondToSliderChange = true;
			this.refreshColors();
			this.myRepaint();
		}
	}

	@Override
	public String saveSettings() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.minHue);
		sb.append(";");
		sb.append(this.maxHue);
		sb.append(";");
		sb.append(this.minSaturation);
		sb.append(";");
		sb.append(this.maxSaturation);
		sb.append(";");
		sb.append(this.minBrightness);
		sb.append(";");
		sb.append(this.maxBrightness);
		return sb.toString();
	}

	@Override
	public void loadSettings(String settings) {
		if(settings == null) return;
		String[] values = settings.split(";");
		if (values.length != 6) return;
		try{
			this.minHue = Float.parseFloat(values[0]);
			this.maxHue = Float.parseFloat(values[1]);
			this.minSaturation = Float.parseFloat(values[2]);
			this.maxSaturation = Float.parseFloat(values[3]);
			this.minBrightness = Float.parseFloat(values[4]);
			this.maxBrightness = Float.parseFloat(values[5]);
			this.respondToSliderChange = false;
			this.recalculateSliders();
			this.respondToSliderChange = true;
			this.refreshColors();
			this.myRepaint();
		}catch (Exception ex){
			return;
		}
	}
	
}
