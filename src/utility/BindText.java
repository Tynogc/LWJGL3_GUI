package utility;

import menu.FontRenderer;

public class BindText {
	
	public enum BINDING{
		BIND_LEFT, BIND_RIGHT, CENTERED, ELEGANT_RIGHT, ELEGANT_LEFT
	}

	public static int getTextZeroPosition(String s, FontRenderer f, int xSize, BINDING b){
		int w = f.getStringWidth(s);
		
		int golden = (int)(0.382*xSize);
		
		switch (b) {
		case BIND_LEFT:
			return 10;
		case BIND_RIGHT:
			return xSize-10-w;
		case ELEGANT_LEFT:
			if(golden-w/2>10)
				return golden-w/2;
			return 10;
		case ELEGANT_RIGHT:
			if(golden-w/2>10)
				return xSize-golden-w/2;
			return xSize-10-w;
		case CENTERED:
			return xSize/2-w/2;
		default:
			return 0;
		}
	}
}
