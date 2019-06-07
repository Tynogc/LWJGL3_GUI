package menu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.SpriteBatch;
import main.graphics.Texture;
import main.graphics.TextureRegion;

public class FontRenderer {

	private static List<FontRenderer> fonts;
	
	public Map<Character, Glyph> glyphs;
	
	public final String name;
	private int lineOffset;
	private int height;
	private int addOffset;
	
	public static Texture tex;
	
	class Glyph{
		
		public TextureRegion region;
		public char ch;
		public int distance;
		
		public Glyph(){
		}
	}
	
	public void render(SpriteBatch sp, String s, int x, int y){
		if(s == null)
			return;
		y-=lineOffset;
		for (int i = 0; i < s.length(); i++) {
			Glyph gl = glyphs.get(s.charAt(i));
			if(gl == null)
				gl = glyphs.get('?');
			sp.draw(gl.region, x, y);
			x+=gl.distance+addOffset;
		}
	}
	
	public int getStringWidth(String s){
		int x = 0;
		if(s == null)
			return 0;
		if(s.length() == 0)
			return 0;
		for (int i = 0; i < s.length(); i++) {
			Glyph gl = glyphs.get(s.charAt(i));
			if(gl == null)
				gl = glyphs.get('?');
			x+=gl.distance+addOffset;
		}
		return x;
	}
	
	public int getStringWidthSpecial(String s, char follow){
//		int x = 0;
//		if(s == null)
//			return 0;
//		if(s.length() == 0)
//			return 0;
//		Glyph gl = null;
//		for (int i = 0; i < s.length(); i++) {
//			gl = glyphs.get(s.charAt(i));
//			if(gl == null)
//				gl = glyphs.get('?');
//			if(i < s.length()-1)
//				x+=gl.getDistanceTo(s.charAt(i+1));
//		}
//		return x+gl.getDistanceTo(follow);
		return getStringWidth(s);
	}
	
	public int getStringHeight(){
		return height;
	}
	
	private FontRenderer(String n, Texture tex) throws Exception{
		name = n;
		
		glyphs = new HashMap<>();
		
		FileReader fr = new FileReader("res/font/map/"+name+".texFnt");
		BufferedReader br = new BufferedReader(fr);
		Glyph currGL = null;
		String s;
		while ((s = br.readLine()) != null) {
			if(s.startsWith("Line:")){
				lineOffset = Integer.parseInt(s.substring(5));
			}
			if(s.startsWith("Hgh:")){
				height = Integer.parseInt(s.substring(4));
			}
			if(s.startsWith("Offset:")){
				addOffset = Integer.parseInt(s.substring(7));
			}
			if(s.startsWith("Char:")){
				if(currGL != null)
					glyphs.put(currGL.ch, currGL);
				s = s.substring(5);
				currGL = new Glyph();
				currGL.ch = s.charAt(0);
				s = s.substring(2);
				String[] q = s.split(" ");
				currGL.region = new TextureRegion(tex, Integer.parseInt(q[0]), Integer.parseInt(q[1]),
						Integer.parseInt(q[2]),Integer.parseInt(q[3]));
				if(q.length>4)
				currGL.distance = Integer.parseInt(q[4]);
			}
			if(currGL == null)
				continue;
			if(s.startsWith("Dis:")){
				s = s.substring(4);
				currGL.distance = Integer.parseInt(s.substring(2));
			}
		}
		if(currGL != null)
			glyphs.put(currGL.ch, currGL);
		
		br.close();
	}
	
	public static void init() throws Exception{
		fonts = new ArrayList<>();
		tex = new Texture(utility.ResourceLoader.loadResourceURL("res/font/map/tex.png"));
		fonts.add(new FontRenderer("MONO_14", tex));
		fonts.add(new FontRenderer("SANS_14", tex));
		fonts.add(new FontRenderer("SANS_12", tex));
		fonts.add(new FontRenderer("SANS_BOLD_14", tex));
		fonts.add(new FontRenderer("SANS_BLACK_14", tex));
		fonts.add(new FontRenderer("SANS_BLACK_12", tex));
		fonts.add(new FontRenderer("SANS_BOLD_BLACK_14", tex));
		fonts.add(new FontRenderer("SANS_BLACK_22", tex));
		fonts.add(new FontRenderer("SANS_22", tex));
		
		Fonts.init();
	}
	
	public static FontRenderer getFont(String name){
		for (FontRenderer f : fonts) {
			if(f.name.matches(name))
				return f;
		}
		debug.Debug.println("*Error: Font not found - "+name, debug.Debug.ERROR);
		return fonts.get(0);
	}
	
	public static FontRenderer getFont(int id){
		if(id>=fonts.size())return null;
		return fonts.get(id);
	}
	
	public static FontRenderer createCustomFR(String n, Texture t) throws Exception{
		return new FontRenderer(n, t);
	}
	
}
