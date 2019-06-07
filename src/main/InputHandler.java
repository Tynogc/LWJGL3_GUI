package main;

import java.util.ArrayList;
import java.util.List;

import utility.InputEvent;
import utility.InputListenerKey;

import static org.lwjgl.glfw.GLFW.*;
import static main.ContollerInput.*;

public class InputHandler {
	
	private String currentInput;
	public static String staticInput;
	
	private double analogX;
	private double analogY;
	
	private double lastMouseX;
	private double lastMouseY;
	
	public static int mouseXSt;
	public static int mouseYSt;
	
	private double cursorX;
	private double cursorY;
	
	public int mouseX;
	public int mouseY;
	
	public float analogXdir;
	public float analogYdir;
	
	public boolean menuActive = true;
	
	public List<InputMap> inputMap;
	
	private ContollerInput contollerInput;
	
	public boolean[] fKeys = new boolean[12];
	
	public static final int MOUSE_BUTTON = 1000;
	
	public boolean mouseLP;
	public boolean mouseMP;
	public boolean mouseRP;
	
	public boolean mouseLR;
	public boolean mouseMR;
	public boolean mouseRR;
	
	public boolean mouseLD;
	public boolean mouseMD;
	public boolean mouseRD;
	
	public boolean ctrl;
	public boolean shift;
	public boolean alt;
	
	public int mouseScr;
	
	public static InputListenerKey listener;
	
	public InputHandler(){
		currentInput = "";
		inputMap = new ArrayList<>();
		
		inputMap.add(new InputMap(GLFW_KEY_W, CONTROLLER_HATS+200,  'w'));
		inputMap.add(new InputMap(GLFW_KEY_A, CONTROLLER_HATS, 'a'));
		inputMap.add(new InputMap(GLFW_KEY_S, CONTROLLER_HATS+300, 's'));
		inputMap.add(new InputMap(GLFW_KEY_D, CONTROLLER_HATS+100, 'd'));
		inputMap.add(new InputMap(GLFW_KEY_SPACE, CONTROLLER_BUTTON+3, ' '));
		inputMap.add(new InputMap(GLFW_KEY_Q, 'q'));
		inputMap.add(new InputMap(GLFW_KEY_E, 'e'));
		inputMap.add(new InputMap(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT, CONTROLLER_BUTTON+2, 'p'));
		inputMap.add(new InputMap(GLFW_MOUSE_BUTTON_1+MOUSE_BUTTON, 'l'));
		inputMap.add(new InputMap(GLFW_MOUSE_BUTTON_2+MOUSE_BUTTON, 'r'));
	}
	
	public void setController(int ci){
		contollerInput = ContollerInput.getContollerInput(ci);
	}

	public void keyAction(int button, int scan, int action, int mods){
		if(button == GLFW_KEY_F11 && action == GLFW_PRESS) Settings.n_debugInfoToShow = (Settings.n_debugInfoToShow+1)%3;
		
		if(button>=GLFW_KEY_F1 && button<=GLFW_KEY_F12){
			if(action == GLFW_PRESS) fKeys[button-GLFW_KEY_F1] = true;
			if(action == GLFW_RELEASE) fKeys[button-GLFW_KEY_F1] = false;
		}
		
		if(listener != null){
			if(action == GLFW_PRESS || action == GLFW_REPEAT){
				listener.keyTyped(new InputEvent(button, ctrl, shift, alt));
				if(action != GLFW_REPEAT && listener != null) listener.keyPressed(new InputEvent(button, ctrl, shift, alt));
			}
			if(action == GLFW_RELEASE){
				listener.keyReleased(new InputEvent(button, ctrl, shift, alt));
			}
			return;
		}
		
		buttonAction(button, action, mods);
	}
	
	public void keyAction(char c){
		if(listener != null)
			listener.keyTyped(new InputEvent(c, false, false, false));
	}
	
	public void mouseAction(int button, int action, int mods){
		if(button == GLFW_MOUSE_BUTTON_1){
			if(action == GLFW_PRESS){
				mouseLD = true;
				mouseLP = true;
			}else if(action == GLFW_RELEASE){
				mouseLD = false;
				mouseLR = true;
			}
		}
		if(button == GLFW_MOUSE_BUTTON_2){
			if(action == GLFW_PRESS){
				mouseMD = true;
				mouseMP = true;
			}else if(action == GLFW_RELEASE){
				mouseMD = false;
				mouseMR = true;
			}
		}
		if(button == GLFW_MOUSE_BUTTON_3){
			if(action == GLFW_PRESS){
				mouseRD = true;
				mouseRP = true;
			}else if(action == GLFW_RELEASE){
				mouseRD = false;
				mouseRR = true;
			}
		}
		
		buttonAction(button+MOUSE_BUTTON, action, mods);
	}
	
	public void mouseMovement(double x, double y){
		if(menuActive){
			cursorX += x-lastMouseX;
			cursorY += y-lastMouseY;
		}else{
			if(Settings.invertMouseX)analogX += x-lastMouseX;
			else analogX -= x-lastMouseX;
			if(Settings.invertMouseY)analogY += y-lastMouseY;
			else analogY -= y-lastMouseY;
		}
		
		mouseXSt = mouseX = (int)cursorX;
		mouseYSt = mouseY = (int)cursorY;
		
		lastMouseX = x;
		lastMouseY = y;
	}
	
	public void addAnalog(float x, float y){
		if(menuActive){
			cursorX += x;
			cursorY += y;
		}else{
			analogX += x;
			analogY += y;
		}
		
		mouseXSt = mouseX = (int)cursorX;
		mouseYSt = mouseY = (int)cursorY;
	}
	
	public void buttonAction(int button, int action, int mod){
		if(inputMap == null) return;
		//System.out.println(button+" "+action);

		for (InputMap i : inputMap) {
			for (int j = 0; j < i.id.length; j++) {
				if(i.id[j] == button){
					if(action == GLFW_PRESS){
						i.down = true;
					}else if(action == GLFW_RELEASE){
						i.down = false;
					}
					return;
				}
			}
		}
	}
	
	public void fetchInputs(){
		if(contollerInput != null) contollerInput.fetchInputs(this);
		
		currentInput = "";
		for (InputMap i : inputMap) {
			if(i.down) currentInput += i.ch;
			else currentInput += "-";
		}
		staticInput = ""+currentInput;
	}
	
	public void resetKeys(){
		mouseLP = false;
		mouseMP = false;
		mouseRP = false;
		mouseLR = false;
		mouseMR = false;
		mouseRR = false;
	}
	
	public String getCurrentInput() {
		return currentInput;
	}
	
	public double getAnalogX() {
		return analogX;
	}
	
	public double getAnalogY() {
		return analogY;
	}
	
	public static class InputMap{
		private final int[] id;
		private final char ch;
		private boolean down;
		
		public InputMap(int[] i, char c){
			id = i;
			ch = c;
		}
		
		public InputMap(int i, char c){
			id = new int[]{i};
			ch = c;
		}
		
		public InputMap(int i1, int i2, char c){
			id = new int[]{i1, i2};
			ch = c;
		}
		
		public InputMap(int i1, int i2, int i3, char c){
			id = new int[]{i1, i2, i3};
			ch = c;
		}
	}
	
}
