package gui;

import main.graphics.util.Color;
import menu.FontRenderer;

public class VisualTimer {

	private long startTime;
	
	private static final int slides = 16;
	private static final int time = 20;
	
	private FontRenderer font;
	
	public VisualTimer(){
		startTime = System.currentTimeMillis()+time*60000l;
		font = FontRenderer.getFont("SANS_22");
	}
	
	public void render(SpriteBatch sp){
		long t = startTime-System.currentTimeMillis();
		
		t/=1000;
		
		int min = (int)(t/60);
		int sec = (int)(t%60)/10;
		float sli = 1f-(float)t/(time*60f);
		
		if(min>2) sp.setColor(Color.BLACK);
		else sp.setColor(new Color(0,128,0));
		sp.fillRect(0, 0, sp.width, sp.height);
		
		sp.setColor(Color.WHITE);
		sp.scale = 10;
		font.render(sp, min+":"+sec+"0", 10, 30);
		font.render(sp, "S "+(int)(sli*slides+1), 10, 60);
	}
}
