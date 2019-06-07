package gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import main.HigherLevelInput;
import main.InputHandler;
import menu.AbstractMenu;
import menu.ToolTip;

public class MenuHandler {

	private List<AbstractMenu> menus;
	private List<AbstractMenu> menusForRender;
	
	private int mmbX;
	private int mmbY;
	private boolean middDrag;
	private int dragCx;
	private int dragCy;
	
	public MenuHandler(){
		menus = new ArrayList<>();
		menusForRender = new ArrayList<>();
		ToolTip.prepare();
	}
	
	public void addMenu(AbstractMenu m){
		menus.add(0, m);
		menusForRender.add(m);
	}
	
	public void update(InputHandler iph){
		int x = iph.mouseX;
		int y = iph.mouseY;
		boolean onTop = true;
		
		if(iph.mouseMP){
			middDrag = true;
			mmbX = x;
			mmbY = y;
		}
		if(iph.mouseMR)
			middDrag = false;
		
		int scrX = 0;
		int scrY = 0;
		
		if(middDrag){
			dragCx += (x-mmbX)/5;
			dragCy += (y-mmbY)/5;
			if(dragCx > 30 || dragCx < -30){
				scrX = dragCx/30;
				dragCx = dragCx%30;
			}
			if(dragCy > 30 || dragCy < -30){
				scrY = dragCy/30;
				dragCy = dragCy%30;
			}
			x = mmbX;
			y = mmbY;
		}
		
		scrY += iph.mouseScr/60;
		
		for (Iterator<AbstractMenu> i = menus.iterator(); i.hasNext();) {
			AbstractMenu a = i.next();
			
			if(a.rqClose()){
				i.remove();
				menusForRender.remove(a);
				continue;
			}
			
			boolean resetOT = false;
			if(onTop){
				if(a.mouseMoved(x, y)) resetOT = true;
				if(iph.mouseLP)
					a.leftClicked(x, y);
				if(scrX != 0 || scrY != 0)
					a.checkScroll(x, y, scrX, scrY);
			}
			if(iph.mouseLR)
				a.leftReleased(x, y, onTop);
			
			if(resetOT)
				onTop = false;
		}
		
//		if(onTop)
//			hli.update();
//		hli.updateAlways();
		
		iph.mouseLP = false;
		iph.mouseLR = false;
		iph.mouseMP = false;
		iph.mouseMR = false;
		iph.mouseScr = 0;
	}
	
	public void draw(SpriteBatch sp){
		for (AbstractMenu a : menusForRender) {
			a.draw(sp);
		}
		ToolTip.draw(sp);
	}
	
}

