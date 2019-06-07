package menu;

import java.util.ArrayList;
import java.util.List;

import gui.SpriteBatch;

public class AbstractMenu {

	private List<MenuAddable> buttons;
	private List<MenuAddable> toAdd;
	private List<MenuAddable> toRemove;
	
	protected int xPos;
	protected int yPos;
	
	protected int xSize;
	protected int ySize;
	
	private boolean willClose;
	
	protected int lastMouseX;
	protected int lastMouseY;
	
	public AbstractMenu(int x, int y){
		buttons = new ArrayList<>();
		toAdd = new ArrayList<>();
		toRemove = new ArrayList<>();
		xPos = x;
		yPos = y;
	}
	
	public void add(MenuAddable b){
		toAdd.add(b);
	}
	
	public void remove(MenuAddable b){
		toRemove.add(b);
	}
	
	public boolean rqClose(){
		return willClose;
	}
	
	public void leftClicked(int x, int y){
		boolean onTop = true;
		for (MenuAddable b : buttons) {
			if(b.leftPressed(x-xPos, y-yPos, onTop))
				onTop = false;
		}
	}
	
	public void leftReleased(int x, int y, boolean onTop){
		for (MenuAddable b : buttons) {
			if(b.leftReleased(x-xPos, y-yPos, onTop))
				onTop = false;
		}
	}
	
	public boolean mouseMoved(int x, int y){
		boolean onTop = true;
		for (MenuAddable b : buttons) {
			if(b.mouseMoved(x-xPos, y-yPos, onTop))
				onTop = false;
		}
		
		if(!toAdd.isEmpty()){
			for (MenuAddable m : toAdd) {
				buttons.add(m);
			}
			toAdd.clear();
		}
		if(!toRemove.isEmpty()){
			for (MenuAddable m : toRemove) {
				buttons.remove(m);
			}
			toRemove.clear();
		}
		
		lastMouseX = x-xPos;
		lastMouseY = y-yPos;
		
		return isHere(x, y);
	}
	
	public void draw(SpriteBatch sp){
		for (MenuAddable b : buttons) {
			b.draw(sp, xPos, yPos);
		}
		for (MenuAddable b : buttons) {
			b.drawMisc(sp, xPos, yPos);
		}
		drawMisc(sp);
		for (MenuAddable b : buttons) {
			b.drawText(sp, xPos, yPos);
		}
	}
	
	public void checkScroll(int x, int y, int xScr, int yScr){
		for (MenuAddable b : buttons) {
			b.checkScroll(x-xPos, y-yPos, xScr, yScr);
		}
	}
	
	public boolean isHere(int x, int y){
		return x>=xPos && y>=yPos && x<=(xPos+xSize) && y<=(yPos+ySize);
	}
	
	public void closeYou(){
		if(close())
			willClose = true;
	}
	
	protected boolean close(){
		return true;
	}
	
	protected void drawMisc(SpriteBatch sp){
		//Emty; to be overwriten for Textures etc.
	}
}
