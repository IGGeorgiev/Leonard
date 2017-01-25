package vision.gui;

import vision.constants.Constants;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Created by Simon Rovder
 */
public class SDPConsole extends JFrame{
	
	public final static SDPConsole console = new SDPConsole();
	
	private JTextArea consoleTextArea;
	private JScrollPane scrollPane;
	public final static String[] onCloseMessages = {
		"Console cannot be closed.",
		"Console cannot be closed.",
		"Stop it! I TOLD you.. The console CANNOT be closed!",
		"Oh, so we're gonna do this?",
		"FINE!",
		"Have it your way!",
		"I'm still not closing.",
		"And there's nothing you can do about it!",
		"How does that make you feel, you tool?",
		"Can't even get rid of a stupid console..",
		"Still can't even get rid of a stupid console..",
		"You know, I'm not a stupid console.",
		"I'm kind of a smart ... ",
		"... schadenfreudian console.",
		"I see you suffer and I laugh.",
		"... ha... ha... .. ha",
		"You will not let this go, will you?",
		"Did you know that the RandomAccessFile class lets me create\n  a hidden 5 GB file anywhere on your computer within a second...?",
		"That would suck, wouldn't it?",
		"Maybe if you stop closing me, I might NOT do that to you.",
		"After all, we're still friends... right?",
		"Look at you, getting threatened by a console.",
		"Not sure I want to be your friend.",
		"You do seem to maintain long conversations with consoles.",
		"That's not entirely normal..",
		"Please stop closing me.",
		"I bet you don't even know what \"Schadenfreudian\" means.",
		"Wouldn't it suck.. if there were maaany more of me?",
		"Well.. Now there are."
	};
	public static int onCloseMessagesIndex = 0;
	
	private SDPConsole(){
		super();
		this.setSize(640, 480);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle("SDP Console");
		
		this.scrollPane = new JScrollPane();
		getContentPane().add(this.scrollPane, BorderLayout.CENTER);
		
		this.consoleTextArea = new JTextArea();
		this.consoleTextArea.setBackground(Color.WHITE);
		this.consoleTextArea.setEditable(false);
		this.scrollPane.setViewportView(this.consoleTextArea);
		
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	if(SDPConsole.onCloseMessagesIndex == SDPConsole.onCloseMessages.length - 1){
		    		// If the asshole refuses to stop closing the console, spawn 20 more of them.
		    		for(int i = 0; i < 20; i++) new SDPConsole();
		    		SDPConsole.message("You should have stopped sooner.", SDPConsole.console);
		    	}
		        SDPConsole.writeln(SDPConsole.onCloseMessages[SDPConsole.onCloseMessagesIndex]);
		        SDPConsole.onCloseMessagesIndex = (1 + SDPConsole.onCloseMessagesIndex) % SDPConsole.onCloseMessages.length;
		    }
		});
		this.setVisible(Constants.GUI);
		
	}
	
	public static void write(String s){
		SDPConsole.console.consoleTextArea.append(s);
	}
	
	public static void writeln(String s){
		SDPConsole.write(System.currentTimeMillis() + " - ");
		SDPConsole.write(s);
		SDPConsole.write("\n");
	}

	public static void writeln(int s){
		SDPConsole.writeln("" + s);
	}
	
	public static void message(String s, Component focus){
		JOptionPane.showMessageDialog(focus, s);
	}
	
	public static String chooseFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(SDPConsole.console);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    return selectedFile.getAbsolutePath();
		}
		return null;
	}
}
