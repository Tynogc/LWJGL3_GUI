package gui.overview;

import main.graphics.util.Color;
import gui.SpriteBatch;
import menu.AdvancedTextEnterField;
import menu.Button;
import menu.Fonts;
import menu.MoveMenu;
import menu.ScrollBar;
import term.TerminalControl;
import term.TerminalExeption;

public class TerminalMenu extends MoveMenu{

	private term.VisualisedTerminal vt;
	private AdvancedTextEnterField teb;
	private TerminalControl tc;
	private String lastSearch = "zzz";
	
	private String toolTip;
	private String[] preClicks;
	private boolean showClicks = true;
	private int select = -1;
	
	private boolean activ = false;
	private Color egshellColor = new Color(236,223,162);
	
	private String[] lastExec;
	private int lastSelect = -1;
	
	private int toolTipMaxSize = 0;
	private final int terminalYSize;
	
	private final int linesToShow;
	
	private ScrollBar scr;
	private int scrLow;
	private Button scrDo;
	
	public TerminalMenu(int x, int y, term.VisualisedTerminal v, TerminalControl tc) {
		super(x, y, v.xSize+120, 610);
		title = "Terminal";
		vt = v;
		terminalYSize = 550;
		
		this.tc = tc;
		
		lastExec = new String[10];
		for (int i = 0; i < lastExec.length; i++) {
			lastExec[i] = "";
		}
		
		teb = new AdvancedTextEnterField(){
			@Override
			protected void specialKey(int id) {
				if(id == AdvancedTextEnterField.BUTTON_CTRL_SPACE){
					lastSearch = "zzz";
					tryAutoFill(select);
				}
				if(id == AdvancedTextEnterField.BUTTON_DOWN)
					chooseDown();
				if(id == AdvancedTextEnterField.BUTTON_UP)
					chooseUp();
				if(id == AdvancedTextEnterField.BUTTON_ENTER){
					if(!tryAutoFill(select))
						exec();
				}
			}

			@Override
			protected boolean isSpecialChar(char c) {
				if(c != 0)
					lastSelect = -1;
				if(c == ' ' || c == '.')
					return tryAutoFill(select);
				return false;
			}
		};
		
		linesToShow = terminalYSize/term.VisualisedTerminal.LINE_SPACING_Y;
		
		scr = new ScrollBar(xSize-25, 30, ySize-80, term.VisualisedTerminal.LINES, linesToShow, true);
		add(scr);
		scrLow = term.VisualisedTerminal.LINES-linesToShow;
		scr.setScroll(scrLow);
		
		scrDo = new Button(xSize-25, ySize-50, "btnDo") {
			@Override
			protected void isClicked() {
				scr.setScroll(scrLow);
			}
		};
		add(scrDo);
		scr.scrollAreaSizeX = -xSize+40;
	}
	
	@Override
	protected void drawIntern(SpriteBatch sp) {
	}

	@Override
	protected void drawTextIntern(SpriteBatch gr) {
		SpriteBatch g = gr;
		gr.setColor(Color.BLACK);
		gr.fillRect(15+xPos, 30+yPos, xSize-40, 20+terminalYSize);
		vt.render(gr, 15+xPos, 55+yPos, linesToShow, scrLow-scr.getScroll());
		gr.setColor(Color.WHITE);
		gr.setFont(Fonts.font14);
		g.resetClip();
		gr.drawString(teb.text, 30+xPos, terminalYSize+45+yPos);
		if(System.currentTimeMillis()%1000 < 500 && activ){
			int u = Fonts.font14.getStringWidth(teb.text.substring(0, teb.tebpos));
			g.fillRect(30+u+xPos, terminalYSize+33+yPos, 1, 15);
		}
		int xp = 0;
		int yp = teb.text.lastIndexOf(".");
		if(yp < 0) yp = 0;
		else yp = Fonts.font14.getStringWidth(teb.text.substring(0,yp+1));
		if(toolTip != null && activ){
			xp = 14;
			g.setColor(egshellColor);
			g.fillRect(yp+27+xPos, terminalYSize+50+yPos, 6+toolTipMaxSize, 14);
			g.setColor(Color.DARK_GRAY);
			gr.drawString(toolTip, yp+30+xPos, terminalYSize+62+yPos);
			g.drawRect(yp+27+xPos, terminalYSize+50+yPos, 6+toolTipMaxSize, 14);
		}
		if(preClicks != null && showClicks && activ){
			for (int i = 0; i < preClicks.length; i++) {
				g.setColor(egshellColor);
				if(i == select)
					g.setColor(Color.CYAN);
				g.fillRect(yp+27+xPos, terminalYSize+50+i*14+xp+yPos, 6+toolTipMaxSize, 14);
				g.setColor(Color.DARK_GRAY);
				gr.drawString(preClicks[i], yp+30+xPos, terminalYSize+62+i*14+xp+yPos);
			}
			if(preClicks.length > 0)
				g.drawRect(yp+27+xPos, terminalYSize+50+xp+yPos, 6+toolTipMaxSize, 14*preClicks.length);
		}
	}
	
	@Override
	public void leftClicked(int x, int y) {
		super.leftClicked(x, y);
		boolean in = x>=xPos && y>=yPos && x<xPos+xSize && y<yPos+ySize;
		if(in != activ && visible){
			if(in) setFocus();
			else deFocus();
		}
	}

	@Override
	protected boolean close() {
		setVisible(false);
		return false;
	}
	
	@Override
	public void closeYou() {
		setVisible(false);
	}

	private int countToUpdate = 0;
	
	@Override
	protected void update() {
		super.update();
		if(teb.tebpos > teb.text.length())
			return;
		String t = teb.text.substring(0, teb.tebpos);
		
		countToUpdate += main.Timer.timePassed;
		if(t.compareTo(lastSearch) == 0 && countToUpdate < 500)
			return;
		
		countToUpdate = 0;
		
		lastSearch = t;
		
		preClicks = tc.start.getChoosing(t);
		if(preClicks == null)
			preClicks = new String[]{};
		if(select>=preClicks.length)
			select = preClicks.length-1;
		
		toolTip = tc.start.getToolTip(t);
		
		toolTipMaxSize = Fonts.font14.getStringWidth(toolTip);
		for (int i = 0; i < preClicks.length; i++) {
			int u = Fonts.font14.getStringWidth(preClicks[i]);
			if(u>toolTipMaxSize) toolTipMaxSize = u;
		}
	}
	
	public void setFocus(){
		activ = true;
		main.InputHandler.listener = teb;
	}
	
	public void deFocus(){
		if(main.InputHandler.listener == teb)
			main.InputHandler.listener = null;
		activ = false;
	}
	
	private boolean tryAutoFill(int p){
		if(p == -1){
			if(preClicks.length != 1)
				return false;
			p = 0;
		}
		String preText = teb.text.substring(0, teb.tebpos);
		if(p == 0 && preText.endsWith(preClicks[0])){
			return false;
		}
		int q = preText.lastIndexOf(".");
		q++;
		String newT = preText.substring(0, q);
		newT += preClicks[p];
		q = teb.tebpos;
		teb.tebpos += newT.length()-preText.length();
		newT += teb.text.substring(q);
		teb.text = newT;
		select = -1;
		return true;
	}
	
	public void chooseDown(){
		if(lastSelect>=0){
			lastSelect--;
			if(lastSelect<0)
				teb.text = "";
			else
				teb.text = lastExec[lastSelect];
			
			teb.tebpos = teb.text.length();
			return;
		}
		select++;
		if(select>=preClicks.length-1)
			select = preClicks.length-1; 
	}
	
	public void chooseUp(){
		if((teb.tebpos == 0 || lastSelect != -1)&&select<0){
			if(lastSelect == -1)
				lastSelect++;
			else if(lastExec[lastSelect].length()>0)
				lastSelect++;
			if(lastSelect>=lastExec.length)
				lastSelect=lastExec.length-1;
			teb.text = lastExec[lastSelect];
			
			teb.tebpos = teb.text.length();
			return;
		}
		select--;
		if(select<-1)
			select = -1;
	}
	
	private void exec(){
		if(teb.text.length()<=0)
			return;
		vt.println("*EXEC: "+teb.text, debug.Debug.PRICOM);
		try {
			tc.start.exec(teb.text, vt);
		} catch (TerminalExeption e) {
			vt.println("Terminal Exeption: "+e.getMessage(), debug.Debug.COMERR);
		}
		
		for (int i = lastExec.length-2; i >= 0; i--) {
			lastExec[i+1] = lastExec[i];
		}
		lastExec[0] = teb.text;
		lastSelect = -1;
		
		teb.tebpos = 0;
		teb.text = "";
	}
	
	public void toggleVisible(){
		setVisible(!visible);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(!visible)deFocus();
	}

}
