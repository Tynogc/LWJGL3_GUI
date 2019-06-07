package menu;

import gui.SpriteBatch;

public interface MenuAddable {

	public boolean leftPressed(int x, int y, boolean onTop);
	public boolean leftReleased(int x, int y, boolean onTop);
	public boolean mouseMoved(int x, int y, boolean onTop);
	
	public void checkScroll(int x, int y, int xScr, int yScr);
	
	public void draw(SpriteBatch sp, int x, int y);
	public void drawMisc(SpriteBatch sp, int x, int y);
	public void drawText(SpriteBatch sp, int x, int y);
}
