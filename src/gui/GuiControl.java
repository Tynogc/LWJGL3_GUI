package gui;

import main.InputHandler;
import main.graphics.ITexture;
import main.graphics.Render3D;
import main.graphics.ShaderProgram;
import main.graphics.Texture;
import main.graphics.obj.AttributeData;
import main.graphics.obj.Mesh_Dynamic;
import main.graphics.util.Color;
import menu.FontRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import debug.OnScreenText;

public class GuiControl extends SpriteBatch{

	private Matrix4f matIso;
	
	private Mesh_Dynamic mesh;
	
	public static final int DRAW_SIZE = 3000;
	
	private Texture currTexture;
	
	private ShaderProgram guiShader;
	
	private FontRenderer font;
	
	private OnScreenText ost;
	
	private MenuHandler menus;
	
	public GuiControl(){
		matIso = new Matrix4f();
	}
	
	public void init() throws Exception{
		main.Window.addResizeListener(new main.Window.ResizeListener() {
			@Override
			public void resize(int w, int h) {
				utility.MathUtility.mvtProjection(matIso, 0, w, 0, h);
				
				width = w;
				height = h;
			}
		});
		
		mesh = new Mesh_Dynamic(new AttributeData[]{
				new AttributeData(0, 3),
				new AttributeData(1, 4),
				new AttributeData(2, 2)
		}, DRAW_SIZE);
		
		guiShader = new ShaderProgram("res/sha/gui");
		guiShader.bind();
		guiShader.setUniformI("tex1", 0);
		guiShader.setUniformI("tex2", 1);
		guiShader.setUniformI("tex3", 2);
		guiShader.setUniformI("tex4", 3);
		guiShader.setUniformI("u_mode", 0);
		
		font = FontRenderer.getFont("MONO_14");
		ost = new OnScreenText();
		
		PicLoader.pic = new PicLoader("res/ima/gui/gui");
		
		menus = new MenuHandler();
		menus.addMenu(new gui.overview.TerminalMenu(0, 200, debug.Debug.term, new term.TerminalControl()));
	}
	
	public void update(InputHandler iph){
		menus.update(iph);
	}
	
	public void prepareGUIrender(){
		guiShader.bind();
		currTexture = null;
		guiShader.setUniformM("u_transformation", matIso);
	}
	
	public void renderGUI(){
		menus.draw(this);
		
		setColor(Color.WHITE);
		font.render(this, "FPS:"+main.Timer.fps, 3, 15);
		font.render(this, generateRAM(), 3, 25);
//		font.render(this, "R3D Move:"+main.graphics.tec.RenderPipeline.timeMove/1000+
//				"us Frustum:"+main.graphics.tec.RenderPipeline.timeFrust/1000+"us Collision:"+
//				game.GameControl.getThreadTimingStatic()/1000+"us", 3, 35);
//		font.render(this, "Input:"+game.GameControl.getLastInput(), 3, 45);
		if(main.Settings.n_debugInfoToShow == 0)debug.PerformanceMonitor.timeSimple.draw(3, 55, this, font);
		if(main.Settings.n_debugInfoToShow == 1)debug.PerformanceMonitor.timeCplx.draw(3, 55, this, font);
		if(main.Settings.n_debugInfoToShow == 2)debug.PerformanceMonitor.timeGpu.draw(3, 55, this, font);
		ost.paint(this, font);
		
		//TEST
//		double ang = (double)(main.Timer.currentTime%5000)/5000.0*3.1415*2;
//		
//		for (int i = 0; i < 30; i++) {
//			for (int j = 0; j < 30; j++) {
//				checkFlush();
//				vertex((float)(Math.sin(ang)*0.5)*100+200+i*5, (float)(Math.cos(ang)*0.5)*100+200+j*5, 0.0f,Color.WHITE);
//				vertex((float)(Math.sin(ang+1.047*2)*0.5)*100+200+i*5, (float)(Math.cos(ang+1.047*2)*0.5)*100+200+j*5, 0.0f, Color.GREEN);
//				vertex((float)(Math.sin(ang-1.047*2)*0.5)*100+200+i*5, (float)(Math.cos(ang-1.047*2)*0.5)*100+200+j*5, 0.0f, Color.BLUE);
//			}
//		}
		
	}
	
	public void checkFlush(ITexture nextTexture){
		boolean fl = false;
		boolean textureChanged = false;
		if(currTexture == null && nextTexture != null){
			setTexture(nextTexture.getTexture());
		}else if(nextTexture != null){
			if(nextTexture.getTexture() != currTexture){
				fl = true;
				textureChanged = true;
			}
		}
		
		if(mesh.size <= mesh.currentVertexCount+6)
			fl = true;
		
		if(fl){
			flush();
			if(textureChanged)
				setTexture(nextTexture.getTexture());
		}
	}
	
	public void setTexture(Texture t){
		Render3D.setCurrentTexture(t);
		currTexture = t;
	}
	
	public void checkFlush(){
		if(mesh.size <= mesh.currentVertexCount+6) flush();
	}
	
	public void flush(){
		if(mesh.currentVertexCount > 0)
			mesh.render();
	}
	
	public void dispose(){
		mesh.cleanUp();
	}
	
	public void vertex(float x, float y, float z, Color c){
		vertex(x, y, z, c, 101, 101);
	}
	
	public void vertex(float x, float y, float z, Color c, float u, float v){
		mesh.getBuffer(0).put(x).put(y).put(z);
		mesh.getBuffer(1).put(c.r).put(c.g).put(c.b).put(c.a);
		mesh.getBuffer(2).put(u).put(v);
		
		mesh.currentVertexCount++;
	}
	
	private Runtime runtime = Runtime.getRuntime();
	
	private String generateRAM()
	{
	    long RAM_TOTAL = runtime.totalMemory();
	    long RAM_FREE = runtime.freeMemory();
	    long RAM_MAX = runtime.maxMemory();
	    
	    RAM_TOTAL = RAM_TOTAL / 1024 /1024;
	    RAM_FREE = RAM_FREE / 1024 /1024;
	    RAM_MAX = RAM_MAX / 1024 /1024;
	    
	    double percent = (1.0 * RAM_TOTAL-RAM_FREE) / (1.0 * RAM_MAX) * 100;
	    return "Used:"+(RAM_TOTAL-RAM_FREE)+"MB / Alloc:"+RAM_TOTAL+"MB / Max:"+RAM_MAX+"MB / "+(int)percent+"."+(int)(percent*10)%10+"%";
	}

	@Override
	public void setRenderMode(int mode) {
		flush();
		guiShader.setUniformI("u_mode", mode);
	}
	
	@Override
	public void setRenderAddon(Vector3f v, int u) {
		flush();
		guiShader.setUniformF("u_modeAddon"+u, v);
	}
	
}
