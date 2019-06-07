package menu;

import gui.PicLoader;
import main.graphics.util.Color;
import gui.SpriteBatch;
import main.graphics.TextureRegion;

public abstract class SuperButton extends Button{

	private TextureRegion back;
	
	private TextureRegion icon;
	private int iconOffsetX;
	private int iconOffsetY;
	
	public SuperButton(int x, int y, String t, String icon) {
		super(x, y, t);
		
		back = PicLoader.pic.getImage(t+"BLACK");
		loadIcon(icon);
	}
	
	public void loadIcon(String ic){
		icon = PicLoader.pic.getImage(ic);
		iconOffsetX = (getxSize()-icon.getWidth())/2;
		iconOffsetY = (getySize()-icon.getHeight())/2;
	}
	
	@Override
	public void drawIntern(SpriteBatch sp, int xOff, int yOff) {
		if(isVisible && !isDisabled)
			sp.draw(back, xOff+xPos, yOff+yPos);
		
		super.drawIntern(sp, xOff, yOff);
		
		if(isDisabled){
			sp.setColor(Color.DARK_GRAY);
			sp.draw(icon, xOff+xPos+iconOffsetX, yOff+yPos+iconOffsetY);
			sp.setColor(Color.WHITE);
		}else{
			if(mouseLeft)
				sp.draw(icon, xOff+xPos+iconOffsetX+1, yOff+yPos+iconOffsetY+1);
			else
				sp.draw(icon, xOff+xPos+iconOffsetX, yOff+yPos+iconOffsetY);
		}
	}

}
