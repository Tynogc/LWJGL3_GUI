package menu;

import gui.SpriteBatch;

public abstract class AbstractButton implements MenuAddable{

	protected int xPos;
	protected int yPos;
	
	protected int xSize;
	protected int ySize;
	
	protected int dragX;
	protected int dragY;
	
	protected boolean mouseLeft;
	protected boolean mouseRight;
	protected boolean isFocused;
	
	protected boolean canDragX = false;
	protected boolean canDragY = false;
	
	protected boolean isDisabled;
	protected boolean isVisible = true;
	
	protected boolean isDragging;
	
	private AbstractButton linkedButton;
	
	public AbstractButton(int x, int y){
		xPos = x;
		yPos = y;
	}
	
	public void draw(SpriteBatch sp, int xOff, int yOff){
		if(linkedButton != null)
			linkedButton.draw(sp, xOff, yOff);
		drawIntern(sp, xOff, yOff);
	}
	
	public void drawText(SpriteBatch sp, int xOff, int yOff){
		if(linkedButton != null)
			linkedButton.drawText(sp, xOff, yOff);
		drawTextIntern(sp, xOff, yOff);
	}
	
	public void drawMisc(SpriteBatch sp, int xOff, int yOff){
		if(linkedButton != null)
			linkedButton.drawMisc(sp, xOff, yOff);
		drawMiscIntern(sp, xOff, yOff);
	}
	
	protected abstract void drawIntern(SpriteBatch sp, int xOff, int yOff);
	protected abstract void drawTextIntern(SpriteBatch sp, int xOff, int yOff);
	protected void drawMiscIntern(SpriteBatch sp, int xOff, int yOff){}
	
	public boolean leftPressed(int x, int y, boolean onTop){
		boolean mt = false;
		if(linkedButton != null)
			mt = linkedButton.leftPressed(x, y, onTop);
		
		mouseLeft = isHere(x, y) && onTop && isVisible;
		
		if(mouseLeft){
			dragX = x;
			dragY = y;
			isDragging = true;
		}
		
		return mouseLeft | mt;
	}
	
	public boolean leftReleased(int x, int y, boolean onTop){
		boolean mt = false;
		if(linkedButton != null)
			mt = linkedButton.leftReleased(x, y, onTop);
		
		boolean q = isHere(x, y) && onTop && isVisible && !isDisabled;
		
		if(q && mouseLeft){
			clicked();
		}else{
			unClicked();
		}
		
		mouseLeft = false;
		isDragging = false;
		
		return q | mt;
	}
	
	public boolean mouseMoved(int x, int y, boolean onTop){
		update();
		
		boolean mt = false;
		if(linkedButton != null)
			mt = linkedButton.mouseMoved(x, y, onTop);
		
		if(mouseLeft && isDragging){
			if(canDragX){
				if(x != dragX){
					xPos += x-dragX;
					dragX = x;
					draged();
				}
			}
			if(canDragY){
				if(y != dragY){
					yPos += y-dragY;
					dragY = y;
					draged();
				}
			}
		}
		return ((isFocused = isHere(x, y)) && isVisible) | mt;
	}
	
	protected abstract void clicked();
	
	protected void draged(){
		
	}

	public boolean isHere(int x, int y){
		return x>=xPos && y>=yPos && x<=(xPos+xSize) && y<=(yPos+ySize);
	}
	
	public void link(AbstractButton a){
		linkedButton = a;
	}
	
	protected void update(){}
	
	protected void unClicked(){}
	
	public void checkScroll(int x, int y, int xScr, int yScr){}
	
	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}
}
