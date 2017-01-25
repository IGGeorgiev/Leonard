package vision.tools;

import vision.Vision;
import vision.constants.Constants;
import vision.gui.SDPConsole;
import vision.rawInput.RawInput;
import vision.settings.SettingsManager;
/**
 * Created by Simon Rovder
 */
public class CommandLineParser {
	
	public static final CommandLineParser parser = new CommandLineParser();

	
	private static final String LOAD_ARG   = "--load";
	private static final String CHANNEL_ARG   = "--videoChannel";
	private static final String STREAM_ARG = "-stream";
	private static final String NOGUI = "-noGUI";
	private static final String TIMER = "-timer";
	
	private CommandLineParser(){}
	
	public void newParse(String args[], Vision vision){
		for(int i = 0; i < args.length; i++){
			switch(args[i]){
				case CHANNEL_ARG:
					if(i < args.length - 1){
						RawInput.rawInputMultiplexer.setVideoChannel(Integer.parseInt(args[i+1]));
						i++;
					}
					break;
				case STREAM_ARG:
					RawInput.rawInputMultiplexer.streamVideo();
					break;
				case LOAD_ARG:
					if(i < args.length - 1){
						try {
							SettingsManager.loadSettings(args[i+1]);
						} catch (Exception e) {
							SDPConsole.message("Could not load the file specified in the command line arguments.", SDPConsole.console);
						}
						i++;
					}
					break;
				case NOGUI:
					Constants.GUI = false;
					break;
				case TIMER:
					Constants.TIMER = true;
					break;
				default:
					break;
			}
		}
	}
	
//	public static void apply(String[] args, Vision vision){
//		HashMap<String, String> arguments = new HashMap<String, String>();
//		int i = 0;
//		String key;
//		Integer len;
//
//		while (i < args.length){
//			key = args[i];
//			len = expected.get(key);
//			if(len == null){
//				arguments.put(key, null);
//				i++;
//			} else {
//				if(i + len < args.length){
//					arguments.put(key, args[i+1]);
//				}
//				i = i + 2;
//			}
//		}
//		apply(arguments, vision);
//	}
//
//	public static void apply(HashMap<String, String> arguments, Vision vision){
//		for(String key : arguments.keySet()){
//			System.out.println(key + " : " + arguments.get(key));
//		}
//		if(arguments.containsKey(LOAD_ARG)){
//			try {
//				SettingsManager.loadSettings(arguments.get(LOAD_ARG));
//			} catch (Exception e) {
//				SDPConsole.message("Could not load the file specified in the command line arguments.");
//			}
//		}
//
////		if(arguments.containsKey(HIDDEN_ARG)){
////			vision.setVisible(false);
////			SDPConsole.console.setVisible(false);
////			DistortionPreview.preview.setVisible(false);
////			Preview.preview.setVisible(false);
////		}
//
//		if(arguments.containsKey(CHANNEL_ARG)){
//			RawInput.rawInputMultiplexer.setVideoChannel(Integer.parseInt(arguments.get(CHANNEL_ARG)));
//		}
//
//		if(arguments.containsKey(STREAM_ARG)){
//			RawInput.rawInputMultiplexer.streamVideo();
//		}
//	}
}
