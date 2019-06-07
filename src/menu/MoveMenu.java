package menu;

import gui.PicLoader;
import main.graphics.util.Color;
import gui.SpriteBatch;
import main.graphics.TextureRegion;

public abstract class MoveMenu extends AbstractMenu{

	protected static TextureRegion[][] staticBack;
	protected static TextureRegion[][] staticBackNewStyle;
	protected TextureRegion[][] back;
	protected static TextureRegion lowerBack;
	protected boolean grayBack = true;
	
	protected Button close;
	protected boolean moveAble = true;
	
	private int dragX;
	private int dragY;
	private boolean isDragged;
	
	public String title;
	
	public boolean visible = true;
	
	public MoveMenu(int x, int y, int xS, int yS) {
		super(x, y);
		xSize = xS;
		ySize = yS;
		
		if(staticBack == null){
			staticBack = createBackgroundTexture("menuBack");
			staticBackNewStyle = createBackgroundTexture("menuBackNew");
			lowerBack = PicLoader.pic.getImage("blackStripe");
		}
		
		back = staticBack;
		
		close = new Button(xSize-23, 3, "btnX") {
			@Override
			protected void isClicked() {
				closeYou();
			}
		};
		add(close);
	}
	
	public static TextureRegion[][] createBackgroundTexture(String path){
		TextureRegion tr = PicLoader.pic.getImage(path);
		TextureRegion[][] back = new TextureRegion[3][4];
		for (int i = 0; i < back.length; i++) {
			for (int j = 0; j < back[i].length; j++) {
				back[i][j] = new TextureRegion(tr, i*25, j*25, 25, 25);
			}
		}
		return back;
	}
	
	public void updateSize(){
		close.setxPos(xSize-23);
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if(!visible) return false;
		update();
		if(isDragged){
			if(x != dragX){
				xPos += x-dragX;
				dragX = x;
			}
			if(y != dragY){
				yPos += y-dragY;
				dragY = y;
			}
		}
		return super.mouseMoved(x, y);
	}
	
	protected void update(){}
	
	@Override
	public void leftClicked(int x, int y) {
		if(!visible) return;
		if(moveAble)
		if(x>=xPos && x<=xPos+xSize-25 && y>=yPos && y<=yPos+25){
			dragX = x;
			dragY = y;
			isDragged = true;
		}
		super.leftClicked(x, y);
	}
	
	@Override
	public void leftReleased(int x, int y, boolean onTop) {
		isDragged = false;
		super.leftReleased(x, y, onTop);
	}
	
	@Override
	public void draw(SpriteBatch sp) {
		if(!visible)return;
		sp.setclip(xPos, yPos, xSize, ySize);
		sp.setColor(Color.WHITE);
		sp.draw(lowerBack, xPos+1, yPos+25, xSize-2, ySize-26);
		sp.draw(back[0][0], xPos, yPos);
		sp.draw(back[1][0], xPos+25, yPos, xSize-50, 25);
		sp.draw(back[2][0], xPos+xSize-25, yPos);
		if(grayBack){
			drawAreaBackground(xPos, yPos+25, xSize, ySize-25, sp);
		}
		drawIntern(sp);
		super.draw(sp);
		drawTextIntern(sp);
		sp.resetClip();
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	protected abstract void drawIntern(SpriteBatch sp);
	protected abstract void drawTextIntern(SpriteBatch sp);
	
	protected void drawAreaBackground(int xPos, int yPos, int xSize, int ySize, SpriteBatch sp){
		drawAreaBackground(back, xPos, yPos, xSize, ySize, sp);
	}
	
	public static void drawAreaBackDefaultNewStyle(int xPos, int yPos, int xSize, int ySize, SpriteBatch sp){
		drawAreaBackground(staticBackNewStyle, xPos, yPos, xSize, ySize, sp);
	}
	
	public static void drawAreaBackDefault(int xPos, int yPos, int xSize, int ySize, SpriteBatch sp){
		drawAreaBackground(staticBack, xPos, yPos, xSize, ySize, sp);
	}
	
	public static void drawAreaBackground(TextureRegion[][] back, int xPos, int yPos, int xSize, int ySize, SpriteBatch sp){
		sp.draw(back[0][1], xPos, yPos);
		sp.draw(back[1][1], xPos+25, yPos, xSize-50, 25);
		sp.draw(back[2][1], xPos+xSize-25, yPos);
		
		sp.draw(back[0][2], xPos, yPos+25, 25, ySize-50);
		sp.draw(back[1][2], xPos+25, yPos+25, xSize-50, ySize-50);
		sp.draw(back[2][2], xPos+xSize-25, yPos+25, 25, ySize-50);
		
		sp.draw(back[0][3], xPos, yPos+ySize-25);
		sp.draw(back[1][3], xPos+25, yPos+ySize-25, xSize-50, 25);
		sp.draw(back[2][3], xPos+xSize-25, yPos+ySize-25);
	}
}
