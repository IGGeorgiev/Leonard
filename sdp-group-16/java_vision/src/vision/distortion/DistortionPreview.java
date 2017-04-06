package vision.distortion;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import vision.constants.Constants;
/**
 * Created by Simon Rovder
 */
public class DistortionPreview extends JFrame {
	
	public final JLabel previewLabel;
	
	public static final DistortionPreview preview = new DistortionPreview();
	
	private LinkedList<DistortionPreviewClickListener> listeners;
	
	public void clickHandler(int x, int y){
		for(DistortionPreviewClickListener l : this.listeners){
			l.distortionPreviewClickHandler(x, y);
		}
	}
	
	public static void addDistortionPreviewClickListener(DistortionPreviewClickListener listener){
		DistortionPreview.preview.listeners.add(listener);
	}
	
	private DistortionPreview(){
		super("Distortion Preview");
		this.setSize(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT + 20);
		this.setResizable(false);
		this.listeners = new LinkedList<DistortionPreviewClickListener>();
		this.previewLabel = new JLabel();
		this.getContentPane().add(this.previewLabel);
		this.setVisible(false);
		this.previewLabel.addMouseListener(new MouseListener() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            DistortionPreview.preview.clickHandler(e.getX(), e.getY());
	        }

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
	    });
	}
}
