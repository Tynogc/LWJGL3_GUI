package main;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ContollerInput {
	
	public static final int CONTROLLER_BUTTON = 10000;
	public static final int CONTROLLER_HATS = 11000;
	
	private int analogCam = 1;
	private int analogDir = 0;
	
	private boolean[] buttons;
	private byte[] hats;
	
	public final String name;

	private boolean active;
	
	private ControllerCurve curve;
	
	public static ContollerInput getContollerInput(int controllerId){
		String q = glfwGetJoystickName(controllerId);
		if(q == null)return null;
		debug.Debug.println("* Controller "+q+" bound!", debug.Debug.MESSAGE);
		return new ContollerInput(controllerId, q);
	}
	
	public final int controllerId;
	
	private ContollerInput(int cid, String n){
		controllerId = cid;
		buttons = new boolean[glfwGetJoystickButtons(controllerId).limit()];
		hats = new byte[glfwGetJoystickHats(controllerId).limit()];
		name = n;
		active = true;
		curve = new ControllerCurve();
	}
	
	public void fetchInputs(InputHandler iph){
		if(!active)return;
		FloatBuffer f = glfwGetJoystickAxes(controllerId);
		if(f == null){
			debug.Debug.println("* Controller "+name+" has stoped Responding! (nd"+controllerId+") = null",
					debug.Debug.ERROR);
			active = false;
			//TODO handle unpluged Controler
			return;
		}

		int num = 0;
		while(f.hasRemaining()){
			float q1 = f.get();
			float q2 = 0;
			if(f.hasRemaining()) q2 = f.get();
			
			if(num == analogCam){
				if(Settings.invertControlerX)q1*=-1;
				if(Settings.invertControlerY)q2*=-1;
				iph.addAnalog(curve.getValue(q1), curve.getValue(q2));
			}
			if(num == analogDir){
				iph.analogXdir = q1;
				iph.analogYdir = q2;
			}
			num++;
		}
		
		int i = 0;
		ByteBuffer b = glfwGetJoystickButtons(controllerId);
		while (b.hasRemaining()) {
			if(buttons[i] != (b.get() == GLFW_PRESS)){
				buttons[i] = !buttons[i];
				if(buttons[i])iph.buttonAction(CONTROLLER_BUTTON+i, GLFW_PRESS, 0);
				else iph.buttonAction(CONTROLLER_BUTTON+i, GLFW_RELEASE, 0);
			}
			i++;
		}
		
		i = 0;
		b = glfwGetJoystickHats(controllerId);
		while (b.hasRemaining()) {
			byte d = b.get();
			if(hats[i] != d){
				hats[i] = d;
				if((d & GLFW_HAT_LEFT) != 0)iph.buttonAction(CONTROLLER_HATS+i, GLFW_PRESS, 0);
				else iph.buttonAction(CONTROLLER_HATS+i, GLFW_RELEASE, 0);
				if((d & GLFW_HAT_RIGHT) != 0)iph.buttonAction(CONTROLLER_HATS+i+100, GLFW_PRESS, 0);
				else iph.buttonAction(CONTROLLER_HATS+i+100, GLFW_RELEASE, 0);
				if((d & GLFW_HAT_UP) != 0)iph.buttonAction(CONTROLLER_HATS+i+200, GLFW_PRESS, 0);
				else iph.buttonAction(CONTROLLER_HATS+i+200, GLFW_RELEASE, 0);
				if((d & GLFW_HAT_DOWN) != 0)iph.buttonAction(CONTROLLER_HATS+i+300, GLFW_PRESS, 0);
				else iph.buttonAction(CONTROLLER_HATS+i+300, GLFW_RELEASE, 0);
			}
			i++;
		}
	}
	
	public class ControllerCurve{
		public float[] pos;
		public float[] inten;
		
		private ControllerCurve(){
			pos = new float[]{0, 0.1f, 0.3f, 0.5f, 0.8f, 0.9f, 1};
			inten = new float[]{0, 0, 2.4f, 4, 9, 13, 20};
		}
		
		public float getValue(float n){
			boolean inv = n<0;
			if(inv)n *= -1;
			if(n<pos[1])return 0;
			if(n<1){
				int u = 0;
				for (int i = 0; i < pos.length; i++) {
					if(pos[i]>n){
						u = i;
						break;
					}
				}
				n -= pos[u-1];
				n /= pos[u]-pos[u-1];
				n = (n*inten[u])+(1-n)*inten[u-1];
			}else{
				n*= inten[inten.length-1];
			}
			if(inv)return -n;
			return n;
		}
	}
}
