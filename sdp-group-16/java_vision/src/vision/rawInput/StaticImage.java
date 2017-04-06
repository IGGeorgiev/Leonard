package vision.rawInput;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import vision.constants.Constants;
import vision.gui.Preview;
import vision.gui.SDPConsole;
/**
 * Created by Simon Rovder
 */
public class StaticImage extends AbstractRawInput implements ActionListener{
	private Timer timer;
	private BufferedImage img;
	
	private JButton btnStartInput;
	private JButton btnStopInput;
	private JButton btnBrowse;
	
	private String filePath;
	
	JLabel lblImageSource;
	JTextField textField;
	
	public static final StaticImage staticImage = new StaticImage();
	
	private StaticImage(){
		super();
		this.setLayout(null);
		this.tabName = "Static Image";
		this.timer = new Timer(300, this);
		this.initGUI();
	}
	
	private void initGUI(){
		// TODO: Refactor This
		textField = new JTextField();
		textField.setBounds(150, 25, 311, 20);
		textField.setEnabled(false);
		this.add(textField);
		textField.setColumns(10);
		
		lblImageSource = new JLabel("Image source:");
		lblImageSource.setBounds(10, 28, 129, 14);
		this.add(lblImageSource);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(471, 24, 89, 23);
		this.add(btnBrowse);
		
		btnStartInput = new JButton("Start Input");
		btnStartInput.setBounds(20, 56, 150, 23);
		this.add(btnStartInput);
		
		btnStopInput = new JButton("Stop Input");
		btnStopInput.setBounds(180, 56, 150, 23);
		this.add(btnStopInput);
		
		this.btnStartInput.addActionListener(this);
		this.btnStopInput.addActionListener(this);
		this.btnBrowse.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.timer){
			this.listener.nextFrame(this.img, System.currentTimeMillis());
		}else if(e.getSource() == this.btnStartInput){
			this.start();
		}else if(e.getSource() == this.btnStopInput){
			this.stop();
		}else if(e.getSource() == this.btnBrowse){
			String newFilePath = SDPConsole.chooseFile();
			if(newFilePath != null){
				this.filePath = newFilePath;
				this.textField.setText(this.filePath);
			}
		}
	}

	@Override
	public void stop() {
		this.timer.stop();
		this.btnBrowse.setEnabled(true);
	}

	@Override
	public void start() {
		try {
		    this.img = ImageIO.read(new File(filePath));
		    if(this.img.getWidth() != Constants.INPUT_WIDTH || this.img.getHeight() != Constants.INPUT_HEIGHT){
		    	SDPConsole.message("The image you tried to open is not the correct dimensions. The dimensions are supposed to be " + Constants.INPUT_WIDTH + " by " + Constants.INPUT_HEIGHT + "!", this);
		    	return;
		    }
			this.timer.start();
			this.btnBrowse.setEnabled(false);
			Preview.preview.setVisible(true);
		} catch (Exception e) {
			SDPConsole.message("Could not open the image. Something went wrong. Try JPG and JPEG images of size 640 by 480.", this);
		}
	}
	
	
}
