package vision.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;

import vision.RobotAlias;
import vision.RobotType;
import vision.colorAnalysis.SDPColor;
import vision.distortion.Distortion;
import vision.robotAnalysis.RobotColorSettings;
import vision.settings.SaveLoadCapable;
import vision.settings.SettingsManager;

import static vision.RobotType.*;

/**
 * Created by Simon Rovder
 */
public class MiscellaneousSettings extends JPanel implements ActionListener, SaveLoadCapable{


	public static final HashMap<RobotType, JComboBox<RobotAlias>> aliases = new HashMap<>();

	public static final MiscellaneousSettings miscSettings = new MiscellaneousSettings();
	
	private JButton saveSettings;
	private JButton loadSettings;
	private JCheckBox flipPitch;
	private JCheckBox friendsAreYellow;
	private JCheckBox friendOneIsGreen;
	private JCheckBox foeOneIsGreen;
	private JCheckBox assumeYellow;

	
	private MiscellaneousSettings(){
		super();
		this.setLayout(null);
		
		this.saveSettings = new JButton("Save Settings");
		this.saveSettings.setBounds(10, 30, 150, 30);
		this.add(this.saveSettings);
		this.saveSettings.addActionListener(this);
		
		this.loadSettings = new JButton("Load Settings");
		this.loadSettings.setBounds(10, 70, 150, 30);
		this.add(this.loadSettings);
		this.loadSettings.addActionListener(this);

		this.flipPitch = new JCheckBox("Flip Pitch");
		this.flipPitch.setBounds(10, 110, 200, 30);
		this.flipPitch.addActionListener(this);
		this.add(this.flipPitch);

		this.friendsAreYellow = new JCheckBox("Friends are Yellow");
		this.friendsAreYellow.setBounds(10, 140, 200, 30);
		this.friendsAreYellow.addActionListener(this);
		this.add(this.friendsAreYellow);

		this.friendOneIsGreen = new JCheckBox("Friend One is Green");
		this.friendOneIsGreen.setBounds(10, 170, 200, 30);
		this.friendOneIsGreen.addActionListener(this);
		this.add(this.friendOneIsGreen);

		this.foeOneIsGreen = new JCheckBox("Foe One is Green");
		this.foeOneIsGreen.setBounds(10, 200, 200, 30);
		this.foeOneIsGreen.addActionListener(this);
		this.add(this.foeOneIsGreen);

		this.assumeYellow = new JCheckBox("Assume Yellow");
		this.assumeYellow.setBounds(220, 110, 200, 30);
		this.assumeYellow.addActionListener(this);
		this.add(this.assumeYellow);

		int offset = 0;

		for(RobotType type : RobotType.values()){


			JLabel label = new JLabel(type.toString());
			label.setBounds(10,230 + offset,300,30);
			this.add(label);

			JComboBox<RobotAlias> selection = new JComboBox<>(RobotAlias.values());
			selection.setBounds(230,230 + offset,300,30);
			selection.setSelectedItem(RobotAlias.UNKNOWN);
			this.add(selection);


			offset += 30;

			MiscellaneousSettings.aliases.put(type, selection);
		}
	}

	private void checkBoxesToValues(){
		Distortion.ROTATE_PITCH = this.flipPitch.isSelected();
		RobotColorSettings.FRIEND_COLOR = this.friendsAreYellow.isSelected() ? SDPColor.YELLOW : SDPColor.BLUE;
		RobotColorSettings.FOE_COLOR = this.friendsAreYellow.isSelected() ? SDPColor.BLUE : SDPColor.YELLOW;
		RobotColorSettings.FRIEND_1_IS_GREEN = this.friendOneIsGreen.isSelected();
		RobotColorSettings.FOE_1_IS_GREEN = this.foeOneIsGreen.isSelected();
		RobotColorSettings.ASSUME_YELLOW = this.assumeYellow.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.saveSettings){
			try {
				SettingsManager.saveSettings();
			} catch (Exception e1) {
				e1.printStackTrace();
				SDPConsole.message("Cannot save settings.", this);
			}
		} else if(e.getSource() == this.loadSettings){
			try {
				SettingsManager.loadSettings();
			} catch (Exception e1) {
				e1.printStackTrace();
				SDPConsole.message("Cannot load settings.", this);
			}
		}

		this.checkBoxesToValues();
	}

	@Override
	public String saveSettings() {
		StringBuilder b = new StringBuilder();
		b.append(this.flipPitch.isSelected());
		b.append(';');
		b.append(this.friendsAreYellow.isSelected());
		b.append(';');
		b.append(this.friendOneIsGreen.isSelected());
		b.append(';');
		b.append(this.foeOneIsGreen.isSelected());
		b.append(';');
		b.append(this.assumeYellow.isSelected());
		b.append(';');
		b.append(MiscellaneousSettings.aliases.get(FRIEND_1).getSelectedItem().toString());
		b.append(';');
		b.append(MiscellaneousSettings.aliases.get(FRIEND_2).getSelectedItem().toString());
		b.append(';');
		b.append(MiscellaneousSettings.aliases.get(FOE_1).getSelectedItem().toString());
		b.append(';');
		b.append(MiscellaneousSettings.aliases.get(FOE_2).getSelectedItem().toString());

		return b.toString();
	}

	@Override
	public void loadSettings(String settings) {
		String[] set = settings.split(";");
		this.flipPitch.setSelected(Boolean.parseBoolean(set[0]));
		this.friendsAreYellow.setSelected(Boolean.parseBoolean(set[1]));
		this.friendOneIsGreen.setSelected(Boolean.parseBoolean(set[2]));
		this.foeOneIsGreen.setSelected(Boolean.parseBoolean(set[3]));
		this.assumeYellow.setSelected(Boolean.parseBoolean(set[4]));
		MiscellaneousSettings.aliases.get(FRIEND_1).setSelectedItem(RobotAlias.valueOf(set[5]));
		MiscellaneousSettings.aliases.get(FRIEND_2).setSelectedItem(RobotAlias.valueOf(set[6]));
		MiscellaneousSettings.aliases.get(FOE_1).setSelectedItem(RobotAlias.valueOf(set[7]));
		MiscellaneousSettings.aliases.get(FOE_2).setSelectedItem(RobotAlias.valueOf(set[8]));
		this.checkBoxesToValues();
	}
}
