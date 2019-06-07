package term;

import main.graphics.util.Color;
import gui.SpriteBatch;
import menu.FontRenderer;
import static term.TermColors.*; 

public class VisualisedTerminal implements TermPrint{
	
	private long lastTime;
	
	private String[] time;
	private String[][] text;
	private Color[][] textColor;
	private int[][] textSize;
	private int tPos;
	private int currentsize;
	
	private String lastHeader = "";
	
	private FontRenderer font = FontRenderer.getFont("MONO_14");
	
	public static final int LINES = 200;
	public static final int LINE_SPACING_Y = 20;
	
	public final int xSize = 350;
	
	public VisualisedTerminal() {
		text = new String[LINES][0];
		textColor = new Color[LINES][0];
		textSize = new int[LINES][0];
		time = new String[LINES];
		tPos = 0;
		
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void print(String s) {
		print(s, TEXT);
	}

	@Override
	public void println(String s) {
		println(s, TEXT);
	}

	@Override
	public void println(String s, int color) {
		println(s, color, true);
	}
	
	private synchronized void println(String s, int color, boolean showTs){
		String pre = null;
		String[] sq = s.split(Transmitter.HEADER);
		if(sq.length>1){
			pre = sq[0];
			s = s.substring(sq[0].length()+Transmitter.HEADER.length());
		}
		
		tPos++;
		if(tPos >= LINES-1)
			tPos = 0;
		
		if(showTs){
			String tim;
			char c = s.charAt(0);
			if(c == '*' || c == '/' || c == '~'){
				lastTime = System.currentTimeMillis();
				tim = "["+utility.TimeFormat.getTimeDay(lastTime, true)+"]";
			}else{
				String ti = "+"+(System.currentTimeMillis()-lastTime);
				String tii = "[";
				if(ti.length()+tii.length()<8)
					ti+=" ";
				while (ti.length()+tii.length()<9) {
					tii += " ";
				}
				tim = tii+ti+"]";
				s = " "+s;
			}
			time[tPos] = tim;
		}else{
			time[tPos] = "";
		}
		
		text[tPos] = null;
		textColor[tPos] = null;
		currentsize = 0;
		
		if(pre != null){
			lastHeader = pre;
			print(pre+": ", 0xababab);
		}
		
		print(s, color);
	}
	
	@Override
	public synchronized void print(String s, int color) {
		String[] sq = s.split(Transmitter.HEADER);
		if(sq.length>1){
			if(!sq[0].equals(lastHeader))
				println(sq[0]+": ", 0xababab);
			lastHeader = sq[0];
			s = s.substring(sq[0].length()+Transmitter.HEADER.length());
		}
		
		if(currentsize+font.getStringWidth(s) < xSize){
			addString(s, color);
			return;
		}
		splitterSpace(s, color);
	}
	
	private void splitterSpace(String s, int color){
		int index = -100;
		for (int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == ' '){
				if(font.getStringWidth(s.substring(0, i))+currentsize < xSize)
					index = i;
				else break;
			}
		}
		
		if(index>0 && font.getStringWidth(s.substring(0, index))+currentsize > xSize/3){
			addString(s.substring(0, index), color);
			println(" "+s.substring(index), color, false);
			return;
		}
		
		splitterAll(s, color);
	}
	
	private void splitterAll(String s, int color){
		int index = 0;
		while (font.getStringWidth(s.substring(0, index))+currentsize < xSize && index<s.length()-2) {
			index++;
		}
		index--;
		if(index<1)index = 1;
		addString(s.substring(0, index), color);
		println(" "+s.substring(index), color, false);
	}
	
	private void addString(String s, int color){
		int size = font.getStringWidth(s);
		if(text[tPos] == null){
			text[tPos] = new String[]{s};
			textColor[tPos] = new Color[]{new Color(color)};
			textSize[tPos] = new int[]{size};
		}else{
			String[] tn = new String[text[tPos].length+1];
			Color[] tc = new Color[tn.length];
			int[] ts = new int[tn.length];
			for (int i = 0; i < tn.length-1; i++) {
				tn[i] = text[tPos][i];
				tc[i] = textColor[tPos][i];
				ts[i] = textSize[tPos][i];
			}
			tc[tc.length-1] = new Color(color);
			textColor[tPos] = tc; 
			tn[tn.length-1] = s;
			text[tPos] = tn;
			ts[ts.length-1] = size;
			textSize[tPos] = ts;
		}
		currentsize += size;
	}
	
	public synchronized void render(SpriteBatch sp, int x, int y, int nOlines, int offset){
		y+=LINE_SPACING_Y*nOlines;
		
		for (int i = 0; i < nOlines; i++) {
			y-=LINE_SPACING_Y;
			
			int line = tPos-offset-i+LINES;
			line = line%LINES;
			
			if(text[line] == null)continue;
			
			int xq = 0;
			for (int j = 0; j < text[line].length; j++) {
				sp.setColor(textColor[line][j]);
				font.render(sp, text[line][j], xq+x, y);
				xq+=textSize[line][j];
			}
			sp.setColor(Color.WHITE);
			font.render(sp, time[line], x+xSize, y);
		}
	}

}
