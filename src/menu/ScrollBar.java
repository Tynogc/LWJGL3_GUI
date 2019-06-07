package menu;

import gui.PicLoader;
import main.graphics.util.Color;
import gui.SpriteBatch;
import main.graphics.TextureRegion;

public class ScrollBar extends AbstractButton{

	private TextureRegion[] state1;
	private TextureRegion[] state2;
	private TextureRegion[] state3;
	private TextureRegion[] stateFoc;
	private TextureRegion[] stateDis;
	
	private final boolean upDown;
	
	private int size;
	private int barSize;
	private float div;
	
	private int alphaState2;
	private int alphaStateFoc;
	
	private Button buttonPlus;
	private Button buttonMinus;
	
	private int scroll;
	
	private int maxIterations;
	private int barHeightIterations;
	
	private int scrollClickRepeat = 500;
	private long scrollClickRepeatLastTimeU;
	private long scrollClickRepeatLastTimeD;
	
	public int scrollAreaX;
	public int scrollAreaY;
	public int scrollAreaSizeX;
	public int scrollAreaSizeY;
	
	public int scrollScale = 1;
	
	public int scrollLockByButton = 1;
	
	public ScrollBar(int x, int y, int size, int iterations, int barheight, boolean ud) {
		this(x, y, size, iterations, barheight, ud, "bar");
	}
	
	public ScrollBar(int x, int y, int size, int iterations, int barheight, boolean ud, String s) {
		super(x, y);
		
		if(iterations<barheight){
			int i = iterations;
			iterations = barheight;
			barheight = i;
		}
		
		upDown = ud;
		String sld = "LR";
		if(upDown)sld = "UD";
		state1 = generateRegion(PicLoader.pic.getImage(s+sld+"n"));
		state2 = generateRegion(PicLoader.pic.getImage(s+sld+"e"));
		state3 = generateRegion(PicLoader.pic.getImage(s+sld+"c"));
		stateFoc = generateRegion(PicLoader.pic.getImage(s+sld+"f"));
		stateDis = generateRegion(PicLoader.pic.getImage(s+sld+"d"));
		
		this.size = size-40;
		canDragX = !ud;
		canDragY = ud;
		
		setScrSize(barheight, iterations);
		
		if(ud){
			xSize = 20;
			yPos+=20;
			
			buttonMinus = new Button(x, y, "btnUp") {
				@Override
				protected void isClicked() {}
				
				@Override
				protected void update() {
					if(mouseLeft && System.currentTimeMillis()-scrollClickRepeatLastTimeU>scrollClickRepeat){
						scrollClickRepeatLastTimeU = System.currentTimeMillis();
						setScrollByButon(scroll-1);
						scrollClickRepeat -= 100;
						if(scrollClickRepeat<100)scrollClickRepeat = 100;
					}else if(!mouseLeft){
						scrollClickRepeatLastTimeU = 100;
					}
					super.update();
				}
			};
			buttonPlus = new Button(x, y+size-20, "btnDo") {
				@Override
				protected void isClicked() {}
				
				@Override
				protected void update() {
					if(mouseLeft && System.currentTimeMillis()-scrollClickRepeatLastTimeD>scrollClickRepeat){
						scrollClickRepeatLastTimeD = System.currentTimeMillis();
						setScrollByButon(scroll+scrollLockByButton);
						scrollClickRepeat -= 100;
						if(scrollClickRepeat<100)scrollClickRepeat = 100;
					}else if(!mouseLeft){
						scrollClickRepeatLastTimeD = 100;
					}
					super.update();
				}
			};
			
			scrollAreaSizeY = size;
		}else{
			ySize = 20;
			xPos+=20;
			
			buttonMinus = new Button(x, y, "btnL") {
				@Override
				protected void isClicked() {}
				
				@Override
				protected void update() {
					if(mouseLeft && System.currentTimeMillis()-scrollClickRepeatLastTimeU>scrollClickRepeat){
						scrollClickRepeatLastTimeU = System.currentTimeMillis();
						setScrollByButon(scroll-1);
						scrollClickRepeat -= 100;
						if(scrollClickRepeat<100)scrollClickRepeat = 100;
					}else if(!mouseLeft){
						scrollClickRepeatLastTimeU = 100;
					}
					super.update();
				}
			};
			buttonPlus = new Button(x+size-20, y, "btnR") {
				@Override
				protected void isClicked() {}
				
				@Override
				protected void update() {
					if(mouseLeft && System.currentTimeMillis()-scrollClickRepeatLastTimeD>scrollClickRepeat){
						scrollClickRepeatLastTimeD = System.currentTimeMillis();
						setScrollByButon(scroll+scrollLockByButton);
						scrollClickRepeat -= 100;
						if(scrollClickRepeat<100)scrollClickRepeat = 100;
					}else if(!mouseLeft){
						scrollClickRepeatLastTimeD = 100;
					}
					super.update();
				}
			};
			
			scrollAreaSizeX = size;
		}
		
		link(buttonMinus);
		buttonMinus.link(buttonPlus);
		
		scrollAreaX = x;
		scrollAreaY = y;
	}
	
	private TextureRegion[] generateRegion(TextureRegion tr){
		if(upDown)
		return new TextureRegion[]{
				new TextureRegion(tr, 0, 0, tr.getWidth(), 4),
				new TextureRegion(tr, 0, 4, tr.getWidth(), 20),
				new TextureRegion(tr, 0, 26, tr.getWidth(), 4)
		};
		return new TextureRegion[]{
				new TextureRegion(tr, 0, 0, 4, tr.getHeight()),
				new TextureRegion(tr, 4, 0, 20, tr.getHeight()),
				new TextureRegion(tr, 26, 0, 4, tr.getHeight())
		};
	}

	@Override
	protected void drawIntern(SpriteBatch sp, int xOff, int yOff) {
		sp.setColor(Color.WHITE);
		if(isDisabled){
			singleDraw(sp, stateDis, xOff, yOff);
			return;
		}
		singleDraw(sp, state1, xOff, yOff);
		int fs1 = getFocusShine1(isFocused || buttonMinus.isFocused || buttonPlus.isFocused || mouseLeft);
		if(fs1>0){
			sp.setColor(new Color(255,255,255,fs1/4));
			singleDraw(sp, state2, xOff, yOff);
		}
		if(mouseLeft){
			sp.setColor(Color.WHITE);
			singleDraw(sp, state3, xOff, yOff);
		}
		fs1 = getFocusShine2(isFocused);
		if(fs1>0){
			sp.setColor(new Color(255,255,255,fs1/4));
			singleDraw(sp, stateFoc, xOff, yOff);
		}
	}
	
	private void singleDraw(SpriteBatch sp, TextureRegion[] tr, int atX, int atY){
		if(upDown){
			atY += snapToPosition();
			atX += xPos;
			sp.draw(tr[0], atX, atY);
			sp.draw(tr[1], atX, atY+4, tr[1].getWidth(), barSize-8);
			sp.draw(tr[2], atX, atY+barSize-4);
		}else{
			atX += snapToPosition();
			atY += yPos;
			sp.draw(tr[0], atX, atY);
			sp.draw(tr[1], atX+4, atY, barSize-8, tr[1].getHeight());
			sp.draw(tr[2], atX+barSize-4, atY);
		}
	}
	
	private int getFocusShine1(boolean b){
		if(b && alphaState2<1023){
			alphaState2 += main.Timer.timePassed*2;
			if(alphaState2>1023)
				alphaState2 = 1023;
		}else if(!b && alphaState2>0){
			alphaState2 -= main.Timer.timePassed;
			if(alphaState2<0)
				alphaState2 = 0;
		}
		return alphaState2;
	}
	
	private int getFocusShine2(boolean b){
		if(b && alphaStateFoc<1023){
			alphaStateFoc += main.Timer.timePassed;
			if(alphaStateFoc>1023)
				alphaStateFoc = 1023;
		}else if(!b && alphaStateFoc>0){
			alphaStateFoc -= main.Timer.timePassed;
			if(alphaStateFoc<0)
				alphaStateFoc = 0;
		}
		return alphaStateFoc;
	}
	
	@Override
	public void checkScroll(int x, int y, int xScr, int yScr) {
		if(scrollAreaSizeX == 0 || scrollAreaSizeY == 0)return;
		
		//yScr += dirScr;
		boolean b;
		if(scrollAreaSizeX>0){
			b = x>=scrollAreaX && x<scrollAreaX+scrollAreaSizeX;
		}else{
			b = x>=scrollAreaX+scrollAreaSizeX && x<scrollAreaX+20;
		}
		if(scrollAreaSizeY>0){
			b = b&& y>=scrollAreaY && y<scrollAreaY+scrollAreaSizeY;
		}else{
			b = b&& y>=scrollAreaY+scrollAreaSizeY && y<scrollAreaY;
		}
		if(b){
			if(upDown){
				setScroll(scroll+yScr*scrollScale);
			}else{
				setScroll(scroll+xScr*scrollScale);
			}
			
			alphaState2 += main.Timer.timePassed*20;
			if(alphaState2>1023)
				alphaState2 = 1023;
		}
	}

	@Override
	protected void drawTextIntern(SpriteBatch sp, int xOff, int yOff) {
	}

	@Override
	protected void clicked() {
		
	}
	
	@Override
	protected void draged() {
		if(buttonMinus.xPos+20>xPos && !upDown)xPos = buttonMinus.xPos+20;
		if(buttonMinus.yPos+20>yPos && upDown)yPos = buttonMinus.yPos+20;
		if(buttonMinus.xPos+20+size-barSize<xPos)xPos = buttonMinus.xPos+20+size-barSize;
		if(buttonMinus.yPos+20+size-barSize<yPos)yPos = buttonMinus.yPos+20+size-barSize;
		setScrrollMLx();
	}
	
	public void setScrSize(int barheight, int iterations){
		div = (float)this.size/iterations;
		barSize = (int)(div*barheight);
		if(upDown){
			ySize = barSize;
		}else{
			xSize = barSize;
		}
		maxIterations = iterations;
		barHeightIterations = barheight;
	}
	
	@Override
	public boolean leftReleased(int x, int y, boolean onTop) {
		if(upDown){
			yPos = snapToPosition();
		}else{
			xPos = snapToPosition();
		}
		scrollClickRepeat = 500;
		return super.leftReleased(x, y, onTop);
	}
	
	private int snapToPosition(){
		if(upDown){
			return(int)((float)(buttonMinus.yPos+20)+scroll*div);
		}else{
			return(int)((float)(buttonMinus.xPos+20)+scroll*div);
		}
	}
	
	public int getScroll(){
		return scroll;
	}
	
	protected void wasScrolled(){
		
	}
	
	private void setScrrollMLx(){
		if(upDown){
			scroll = (int)((float)(yPos-buttonMinus.yPos-20)/div);
		}else{
			scroll = (int)((float)(xPos-buttonMinus.xPos-20)/div);
		}
		if(scroll>maxIterations-barHeightIterations)scroll = maxIterations-barHeightIterations;
		
		wasScrolled();
	}
	
	public void setScroll(int scr){
		scroll = scr;
		if(scroll<0)scroll = 0;
		if(scroll>maxIterations-barHeightIterations)scroll = maxIterations-barHeightIterations;
		if(upDown)
			yPos = snapToPosition();
		else
			xPos = snapToPosition();
		
		wasScrolled();
	}
	
	public void setScrollByButon(int scr){
		scr /= scrollLockByButton;
		scr *= scrollLockByButton;
		setScroll(scr);
	}
	
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		buttonPlus.isDisabled = disabled;
		buttonMinus.isDisabled = disabled;
	}

}
