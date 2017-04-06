package vision.rawInput;

import javax.swing.JPanel;
/**
 * Created by Simon Rovder
 */
abstract class AbstractRawInput extends JPanel implements RawInputInterface{
	
	protected RawInput listener;
	private boolean active;
	public String tabName;
	
	public void setInputListener(RawInput listener){
		this.listener = listener;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean isActive(){
		return this.active;
	}
	
	public String getTabName(){
		return this.tabName;
	}
}
