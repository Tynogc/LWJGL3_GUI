package main.graphics.obj;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import main.graphics.util.Color;
import menu.FontRenderer;

public class ProjectionPoint {

	public Vector3f positionRaw;
	
	private Vector4f positionProjected;
	
	public String display;
	
	public FontRenderer font;
	
	public Color color = Color.WHITE;
	
	public ProjectionPoint(float x, float y, float z, String d){
		positionRaw = new Vector3f(x,y,z);
		positionProjected = new Vector4f();
		display = d;
		font = FontRenderer.getFont("MONO_14");
	}
	
	public void project(Matrix4f cam, Matrix4f tr, gui.SpriteBatch sp){
		positionProjected.set(positionRaw, 1);
		if(tr != null) tr.transform(positionProjected);
		cam.transform(positionProjected);
		
		if(positionProjected.w <= 0) return;
		float x = positionProjected.x / positionProjected.w;
		float y = positionProjected.y / positionProjected.w;
		
		int x1 = (sp.getWidth()+(int)(x*sp.getWidth()))/2;
		int y1 = (sp.getHeight()-(int)(y*sp.getHeight()))/2;
		
		sp.setColor(color);
		sp.fillRect(x1-10, y1, 21, 1);
		sp.fillRect(x1, y1-10, 1, 21);
		
		if(display != null) if(display.length()>0)
			font.render(sp, display, x1+2, y1-2);
		
		sp.setColor(Color.WHITE);
	}
	
	public void getProjectedPosition(Matrix4f cam, Matrix4f tr, float w, float h, Vector2f result){
		positionProjected.set(positionRaw, 1);
		if(tr != null) tr.transform(positionProjected);
		cam.transform(positionProjected);
		
		if(positionProjected.w <= 0) return;
		float x = positionProjected.x / positionProjected.w;
		float y = positionProjected.y / positionProjected.w;
		
		result.x = (w+x*w)/2;
		result.y = (h-y*h)/2;
	}
}
