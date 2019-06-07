package debug;

import menu.FontRenderer;
import gui.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class OnScreenText {

	private List<String> text;
	
	private static OnScreenText me;
	
	public OnScreenText(){
		text = new ArrayList<>(20);
		me = this;
	}
	
	public static void clear(){
		me.text.clear();
	}
	
	public void paint(SpriteBatch sp, FontRenderer fr){
		int zero = (int)(sp.getWidth()/sp.scale);
		int y = 4;
		int x = 400;
		
		for (String s : text) {
			int z = fr.getStringWidth(s);
			if(x < z)x = z;
		}
		x = zero-x-5;
		for (String s : text) {
			y += 12;
			fr.render(sp, s, x, y);
		}
	}
	
	public static void printText(String t){
		if(me.text.size()<200)
			me.text.add(t);
	}
}
