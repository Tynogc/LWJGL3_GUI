package utility;

public interface InputListenerMouse {

	public void leftClicked(float x, float y);
	public void rightClicked(float x, float y);
	
	public default void leftReleased(float x, float y){}
	public default void rightReleased(float x, float y){}
	
	public void leftDragged(float x, float y);
	public void rightDragged(float x, float y);
	
	public void moved(float x, float y);
}
