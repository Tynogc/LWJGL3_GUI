package menu;

import java.util.ArrayList;
import java.util.List;

import main.graphics.util.Color;
import gui.SpriteBatch;
import main.graphics.TextureRegion;

public class ToolTip {

	private static FontRenderer font;
	
	private static TextureRegion[][] back;
	
	public int xPos;
	public int yPos;
	
	private String[] text;
	
	public boolean obeyMouse;
	
	public ToolTip(){}
	
	public ToolTip(int x, int y){
		xPos = x;
		yPos = y;
	}
	
	public ToolTip(int x, int y, String t){
		xPos = x;
		yPos = y;
		setText(t);
	}
	
	public void setText(String t){
		text = t.split("\n");
	}
	
	public void setText(String[] t){
		text = t;
	}
	
	public void drawIntern(SpriteBatch sp){
		if(text == null)return;
		
		int y = yPos-text.length*15+10;
		int x = xPos;
		
		int size = 0;
		for (String s : text) {
			int u = font.getStringWidth(s);
			if(u>size)size = u;
		}
		
		if(obeyMouse){
			x+=main.InputHandler.mouseXSt;
			y+=main.InputHandler.mouseYSt;
		}
		
		sp.setColor(Color.WHITE);
		
		if(y-15<0)y = 15;
		
		//Background
		drawBack(x, y-15, size+10, text.length*15+5, sp);
		
		for (String s : text) {
			font.render(sp, s, x+5, y);
			y += 15;
		}
	}
	
	private void drawBack(int x, int y, int xS, int yS, SpriteBatch sp){
		sp.draw(back[0][0], x, y);
		sp.draw(back[1][0], x+10, y, xS-20, 10);
		sp.draw(back[2][0], x+xS-10, y);
		
		sp.draw(back[0][1], x, y+10, 10, yS-20);
		sp.draw(back[1][1], x+10, y+10, xS-20, yS-20);
		sp.draw(back[2][1], x+xS-10, y+10, 10, yS-20);
		
		sp.draw(back[0][2], x, y+yS-10);
		sp.draw(back[1][2], x+10, y+yS-10, xS-20, 10);
		sp.draw(back[2][2], x+xS-10, y+yS-10);
	}
	
	private static List<ToolTip> toShow;
	
	public static void add(ToolTip t){
		toShow.add(t);
	}
	
	public static void draw(SpriteBatch sp){
		for (ToolTip t : toShow) {
			t.drawIntern(sp);
		}
		toShow.clear();
	}
	
	public static void prepare(){
		font = FontRenderer.getFont("SANS_14");
		toShow = new ArrayList<>();
		
		
		back = new TextureRegion[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				back[i][j] = new TextureRegion(FontRenderer.tex, 26+i*10, 994+j*10, 10, 10);
			}
		}
	}
}
