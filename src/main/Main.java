package main;

import java.io.File;

import org.lwjgl.opengl.*;

import gui.GuiControl;
import main.graphics.Render3D;

import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

	private static Window window;

	public static int width;
	public static int height;

	public static GLCapabilities capabilities;
	
	private Render3D r3d;
	
	private GuiControl gui;
	
	private Timer timer;
	
	private CleanUpProcessor cleanUpProcessor;
	private InputHandler iph;
	
	private int gameSpeed = 1;
	private int singlestep = 0;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		debug.Debug.init();
		
		new File("log/log.txt").mkdirs();
		
		try {
			window = new Window("GL_GUI", 1800, 900, true);
			window.init();
			iph = window.getIph();
			menu.FontRenderer.init();
			
			debug.Debug.term = new term.VisualisedTerminal();
			debug.Debug.println("Starting Engine-Core!");
			
			cleanUpProcessor = new CleanUpProcessor();
			
			r3d = new Render3D();
			r3d.init();
			debug.Debug.println("3D init!");
			
			gui = new GuiControl();
			gui.init();
			r3d.setGui(gui);
			
			timer = new Timer();
			loop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(window != null)
				window.close();
			
			if(r3d != null)
				r3d.cleanup();
			
			cleanUpProcessor.cleanAll();
			// Terminate GLFW
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
		
		System.exit(1);
	}

	private void loop() throws Exception {
		window.setClearColor(0, 0, 0, 1);
		
		while (!window.windowShouldClose()) {
			debug.PerformanceMonitor.timeSimple.start();
			debug.PerformanceMonitor.timeCplx.start();
			debug.PerformanceMonitor.timeGpu.start();
			
			iph.fetchInputs();
			checkFKeys();
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			if(window.isResized()){
				width = window.getWidth();
				height = window.getHeight();
				
				glViewport(0, 0, width, height);
				
				window.knockResize();
				
				window.setResized(false);
			}
			
			int nTime = Math.round(Timer.timePassed/(1000f/60f));
			if(nTime>2)nTime = 1;
			if(nTime<0)nTime = 0;
			
			gui.update(iph);
			
			r3d.prepare();
			
			r3d.render();
			
			debug.PerformanceMonitor.timeGpu.markCPU_done();
			iph.resetKeys();
			window.update();
			timer.waitNextFrame();
			debug.OnScreenText.clear();
			debug.PerformanceMonitor.timeGpu.markSleep_done();
		}
	}

	private void checkFKeys(){
		if(iph.fKeys[4]){
			gameSpeed = 0;
			if(singlestep == 0)singlestep = 1;
		}else if(singlestep == 2)singlestep = 0;
		if(iph.fKeys[5])gameSpeed = 1;
		if(iph.fKeys[6])gameSpeed = 2;
		if(iph.fKeys[7])gameSpeed = 4;
	}
	
	public static void main(String[] args) {
		new Main().run();
	}

}
