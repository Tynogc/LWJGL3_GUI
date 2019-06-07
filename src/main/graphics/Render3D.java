package main.graphics;

import area.LandscapeShader;
import gui.GuiControl;
import main.Window;
import main.graphics.tec.RenderPipeline;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

public class Render3D {

	private LandscapeShader lss;
	
	private static Texture currentTexture;
	private Texture tex;
	
	private GuiControl gui;
	private RenderPipeline pipeline;
	
	private Projection projection;
	private Camera camera;
	private Matrix4f camPro;

	public Render3D() {
		projection = new Projection();
		camera = new Camera();
		camera.pos.z = 4;
		camPro = new Matrix4f();
	}

	public void init() throws Exception {
		debug.PerformanceMonitor.init();
		
		Window.addResizeListener(new Window.ResizeListener() {
			@Override
			public void resize(int w, int h) {
				projection.setAspect(w, h);
			}
		});
		
		lss = new LandscapeShader();
		
		pipeline = new RenderPipeline();
		
		tex = new Texture(utility.ResourceLoader.loadResourceURL("res/test.png"));
	}
	
	public void setGui(GuiControl g){
		gui = g;
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Starts preparing the Pipeline for the next Frame, call as soon as all movement-operations are done.
	 */
	public void prepare() {
		pipeline.prepareRendering(camera, projection);
	}
	
	public void render() {
		projection.getMatrix(camera, camPro);
		clear();
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		
		lss.prepare(camera, projection, camPro);
		
		pipeline.render();
		
		lss.unbind();
		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		debug.PerformanceMonitor.timeSimple.mark("Render 3D");
		
		gui.prepareGUIrender();
		pipeline.render2dProjection(camPro, gui);
		gui.renderGUI();
		gui.flush();
		debug.PerformanceMonitor.timeCplx.mark("Render GUI");
		debug.PerformanceMonitor.timeSimple.mark("Render GUI");
	}
	
	public Camera getCam() {
		return camera;
	}
	
	public RenderPipeline getPipeline(){
		return pipeline;
	}

	public void cleanup() {
		gui.dispose();
		lss.shader.dispose();
		tex.dispose();
	}
	
	public static void setCurrentTexture(Texture t){
		if(t == currentTexture)return;
		currentTexture = t;
		t.bind();
	}
	
	public GuiControl getGUI(){
		return gui;
	}

}
