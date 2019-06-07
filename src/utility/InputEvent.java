package utility;

public class InputEvent {

	public final char keyChar;
	public final int keyID;
	
	public final boolean isControlDown;
	public final boolean isShiftDown;
	public final boolean isAltDown;
	
	public final boolean isKey;
	public final boolean isChar;
	
	public InputEvent(int i, boolean ctrl, boolean shift, boolean alt){
		keyChar = ' ';
		keyID = i;
		isControlDown = ctrl;
		isShiftDown = shift;
		isAltDown = alt;
		
		isKey = true;
		isChar = false;
	}
	
	public InputEvent(char c, boolean ctrl, boolean shift, boolean alt){
		keyChar = c;
		keyID = -1;
		isControlDown = ctrl;
		isShiftDown = shift;
		isAltDown = alt;
		
		isKey = false;
		isChar = true;
	}
	
	public String keyName;
}
