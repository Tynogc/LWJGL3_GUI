package menu;

import gui.SpriteBatch;

public class TextBox extends Container{

	private String[] txt;
	
	private int activeLines;
	
	private FontRenderer render;
	
	private ScrollBar scr;
	
	public TextBox(int x, int y, int xSize) {
		super(x, y);
		
		this.xSize = xSize;
		txt = new String[100];
	}
	
	public void setText(String s, FontRenderer fr, int lines){
		render = fr;
		
		String[] st = s.split(" ");
		String wo = "";
		int c = 0;
		
		for (int i = 0; i < st.length; i++) {
			if(fr.getStringWidth(wo+" "+st[i])>xSize-40){
				txt[c] = wo;
				c++;
				wo = "";
			}
			wo += st[i]+" ";
		}
		
		ySize = (fr.getStringHeight()+5)*lines;
		
		txt[c] = wo;
		activeLines = c+1;
		
		if(scr!=null){
			remove(scr);
		}
		scr = null;
		if(activeLines>lines){
			scr = new ScrollBar(xSize-22, 0, ySize, activeLines*4+2, lines*4, true);
			scr.scrollLockByButton = 4;
			scr.scrollAreaSizeY = ySize;
			scr.scrollAreaSizeX = -xSize+22;
			scr.scrollAreaY-=20;
			add(scr);
		}
	}
	
	@Override
	protected void drawTextIntern(SpriteBatch sp, int x, int y) {
		super.drawTextIntern(sp, x, y);
		
		if(render == null || !visible)
			return;
		
		int off = 0;
		if(scr != null)
			off = scr.getScroll()*(render.getStringHeight()+5);
		off/=4;
		
		for (int i = 0; i < activeLines; i++) {
			render.render(sp, txt[i], x+xPos, y+yPos-off+(render.getStringHeight()+5)*(1+i));
		}
	}
	
	public void setVisible(boolean v){
		visible = v;
	}

}
