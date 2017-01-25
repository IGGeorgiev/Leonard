package vision.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColors;
import vision.distortion.Distortion;
import vision.gui.MiscellaneousSettings;
import vision.gui.SDPConsole;
/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE
 * This class takes care of storing and loading settings. If you add any new features that need
 * callibration or take a long time to set up, edit this class to also save those settings.
 */
public class SettingsManager {


	public static void saveSettings() throws Exception{
		String fileName = SDPConsole.chooseFile();
		if(fileName != null){
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			writer.write("^COLORS\n");
			for(SDPColor key : SDPColor.values()){
				writer.write(key.toString() + "\r\n");
				writer.write(SDPColors.colors.get(key).saveSettings() + "\r\n");
			}
			writer.write("^MISC\r\n");
			writer.write(MiscellaneousSettings.miscSettings.saveSettings() + "\r\n");

			writer.write("^DISTORTION\r\n");
			writer.write(Distortion.distortion.saveSettings() + "\r\n");
			writer.write("^END");
			writer.close();
		}
	}
	
	public static void loadSettings(String fileName) throws Exception{
		if(fileName != null){
			BufferedReader r = new BufferedReader(new FileReader(new File(fileName)));
			String next = r.readLine();
			while(!next.equals("^COLORS")){
				next = r.readLine();
			}
			next = r.readLine();
			while(!next.equals("^MISC")){
				SDPColors.colors.get(SDPColor.valueOf(next)).loadSettings(r.readLine());
				next = r.readLine();
			}
			next = r.readLine();
			while(!next.equals("^DISTORTION")){
				MiscellaneousSettings.miscSettings.loadSettings(next);
				next = r.readLine();
			}
			next = r.readLine();
			Distortion.distortion.loadSettings(next);
			while(!next.equals("^END")) next = r.readLine();
			r.close();
		}
	}
	
	public static void loadSettings() throws Exception{
		String fileName = SDPConsole.chooseFile();
		if(fileName != null){
			loadSettings(fileName);
		}
	}
}
