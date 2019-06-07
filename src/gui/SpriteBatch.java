package gui;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import main.graphics.ITexture;
import main.graphics.Texture;
import main.graphics.TextureRegion;
import main.graphics.util.Color;
import menu.FontRenderer;

public abstract class SpriteBatch {

	public float scale = 1;
	
	protected boolean useMultiColor = false;
	
	protected Color[] colors;
	
	protected int width;
	protected int height;
	
	public float fixedZ = 0;
	
	private FontRenderer font;
	
	public SpriteBatch(){
		colors = new Color[4];
		colors[0] = Color.WHITE;
	}
	
	public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth, float srcHeight,
			float dstX, float dstY) {
		drawRegion(tex, srcX, srcY, srcWidth, srcHeight, dstX, dstY, srcWidth, srcHeight);
	}

	public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth, float srcHeight,
			float dstX, float dstY, float dstWidth, float dstHeight) {
		float u = srcX / tex.getWidth();
		float v = srcY / tex.getHeight();
		float u2 = (srcX + srcWidth) / tex.getWidth();
		float v2 = (srcY + srcHeight) / tex.getHeight();
		draw(tex, dstX, dstY, dstWidth, dstHeight, u, v, u2, v2);
	}
	
	public void drawRegion(TextureRegion region, float srcX, float srcY, float srcWidth, float srcHeight, float dstX, float dstY) {
		drawRegion(region, srcX, srcY, srcWidth, srcHeight, dstX, dstY, srcWidth, srcHeight);
	}
	
	public void drawRegion(TextureRegion region, float srcX, float srcY, float srcWidth, float srcHeight,
			float dstX, float dstY, float dstWidth, float dstHeight) {
		drawRegion(region.getTexture(), region.getRegionX() + srcX, region.getRegionY() + srcY, 
				srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);		
	}

	public void draw(ITexture tex, float x, float y) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight());
	}
	
	public void draw(ITexture tex, float x, float y, float width, float height) {
		draw(tex, x, y, width, height, tex.getU(), tex.getV(), tex.getU2(), tex.getV2());
	}

	
	public void draw(ITexture tex, float x, float y, float originX, float originY, float rotationRadians) {
		draw(tex, x, y, tex.getWidth(), tex.getHeight(), originX, originY, rotationRadians);
	}
	
	public void draw(ITexture tex, float x, float y, float width, float height, 
			float originX, float originY, float rotationRadians) {
		draw(tex, x, y, width, height, originX, originY, rotationRadians, tex.getU(), tex.getV(), tex.getU2(), tex.getV2());
	}
	
	public void draw(ITexture tex, float x, float y, float width, float height, 
			float originX, float originY, float rotationRadians,
			float u, float v,
			float u2, float v2) {
		checkFlush(tex);
		
		if(scale != 1){
			x*=scale;
			y*=scale;
			width *= scale;
			height *= scale;
		}

		float x1,y1, x2,y2, x3,y3, x4,y4;
		
		if (rotationRadians != 0) {
			float cx = originX;
			float cy = originY;
	
			float p1x = -cx;
			float p1y = -cy;
			float p2x = width - cx;
			float p2y = height - cy;
	
			final float cos = (float) Math.cos(rotationRadians);
			final float sin = (float) Math.sin(rotationRadians);
			
			x1 = x + (cos * p1x - sin * p1y) + cx; // TOP LEFT
			y1 = y + (sin * p1x + cos * p1y) + cy;
			x2 = x + (cos * p2x - sin * p1y) + cx; // TOP RIGHT
			y2 = y + (sin * p2x + cos * p1y) + cy;
			x3 = x + (cos * p2x - sin * p2y) + cx; // BOTTOM RIGHT
			y3 = y + (sin * p2x + cos * p2y) + cy;
			x4 = x + (cos * p1x - sin * p2y) + cx; // BOTTOM LEFT
			y4 = y + (sin * p1x + cos * p2y) + cy;
		} else {
			x1 = x;
			y1 = y;
			
			x2 = x+width;
			y2 = y;
			
			x3 = x+width;
			y3 = y+height;
			
			x4 = x;
			y4 = y+height;
		}
		
		if(!useMultiColor){
			// top left, top right, bottom left
			vertex(x1, y1, colors[0], u, v);
			vertex(x2, y2, colors[0], u2, v);
			vertex(x4, y4, colors[0], u, v2);

			// top right, bottom right, bottom left
			vertex(x2, y2, colors[0], u2, v);
			vertex(x3, y3, colors[0], u2, v2);
			vertex(x4, y4, colors[0], u, v2);
		}else{
			// top left, top right, bottom left
			vertex(x1, y1, colors[0], u, v);
			vertex(x2, y2, colors[1], u2, v);
			vertex(x4, y4, colors[3], u, v2);

			// top right, bottom right, bottom left
			vertex(x2, y2, colors[1], u2, v);
			vertex(x3, y3, colors[2], u2, v2);
			vertex(x4, y4, colors[3], u, v2);
		}
	}
	
	public void draw(ITexture tex, float x, float y, float width, float height, float u, float v,
			float u2, float v2) {
		draw(tex, x, y, width, height, x, y, 0f, u, v, u2, v2);
	}
	
	public void drawLine(Vector3f s, Vector3f e, float thick, Color cStart, Color cEnd){
		checkFlush();
		
		Vector3f d = new Vector3f(e);//TODO no new Vectors
		d.sub(s).normalize();
		Vector3f o = new Vector3f().orthogonalize(d).normalize(thick);
		
		vertex(s.x+o.x, s.y+o.y, s.z+o.z, cStart, 101, 101);
		vertex(e.x+o.x, e.y+o.y, e.z+o.z, cEnd, 101, 101);
		vertex(s.x-o.x, s.y-o.y, s.z-o.z, cStart, 101, 101);
		
		vertex(e.x+o.x, e.y+o.y, e.z+o.z, cEnd, 101, 101);
		vertex(e.x-o.x, e.y-o.y, e.z-o.z, cEnd, 101, 101);
		vertex(s.x-o.x, s.y-o.y, s.z-o.z, cStart, 101, 101);
		
		o.rotateAxis((float)(Math.PI/2), d.x, d.y, d.z);
		checkFlush();
		
		vertex(s.x+o.x, s.y+o.y, s.z+o.z, cStart, 101, 101);
		vertex(e.x+o.x, e.y+o.y, e.z+o.z, cEnd, 101, 101);
		vertex(s.x-o.x, s.y-o.y, s.z-o.z, cStart, 101, 101);
		
		vertex(e.x+o.x, e.y+o.y, e.z+o.z, cEnd, 101, 101);
		vertex(e.x-o.x, e.y-o.y, e.z-o.z, cEnd, 101, 101);
		vertex(s.x-o.x, s.y-o.y, s.z-o.z, cStart, 101, 101);
	}
	
	public void fillRect(int x, int y, int xs, int ys){
		checkFlush();
		vertex(x, y, 0, colors[0], 101, 101);
		vertex(x+xs, y, 0, colors[0], 101, 101);
		vertex(x, y+ys, 0, colors[0], 101, 101);
		
		vertex(x+xs, y, 0, colors[0], 101, 101);
		vertex(x+xs, y+ys, 0, colors[0], 101, 101);
		vertex(x, y+ys, 0, colors[0], 101, 101);
	}
	
	public void drawRect(int x, int y, int xs, int ys){
		fillRect(x, y, xs, 1);
		fillRect(x, y, 1, ys);
		fillRect(x, y+ys, xs, 1);
		fillRect(x+xs, y, 1, ys);
	}
	
	public abstract void checkFlush();
	public abstract void checkFlush(ITexture t);
	public abstract void flush();
	
	public void vertex(float x, float y, Color c, float u, float v){
		vertex(x, y, fixedZ, c, u, v);
	}
	
	public abstract void vertex(float x, float y, float z, Color c, float u, float v);
	
	private ClipSet currentClip;
	
	public ClipSet getClip(){
		if(currentClip == null)return new ClipSet(0, 0, width, height);
		return currentClip;
	}
	
	public void setclip(int x, int y, int w, int h){
		flush();
		currentClip = new ClipSet(x, y, w, h);
		GL11.glScissor(x, height-y-h, w, h);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	public void resetClip(){
		flush();
		currentClip = new ClipSet();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public class ClipSet{
		
		public final int x;
		public final int y;
		public final int w;
		public final int h;
		public final boolean active;
		
		private ClipSet(int x, int y, int w, int h){
			this.y=y;
			this.x=x;
			this.w=w;
			this.h=h;
			active = true;
		}
		private ClipSet(){
			x = 0;
			y = 0;
			w = 100000;
			h = 100000;
			active = false;
		}
		
		public void resetToThisClip(){
			if(active)
				setclip(x, y, w, h);
			else
				resetClip();
		}
	}
	
	public void setColor(Color c){
		colors[0] = c;
		useMultiColor = false;
	}
	
	public void setColor(Color[] c){
		for (int i = 0; i < c.length && i < colors.length; i++) {
			colors[i] = c[i];
		}
		useMultiColor = true;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setFont(FontRenderer f){
		font = f;
	}
	
	public void drawString(String s, int x, int y){
		font.render(this, s, x, y);
	}
	
	public abstract void setRenderMode(int mode);
	
	public abstract void setRenderAddon(Vector3f v, int u);
}
