package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class FontCreater {

	private static final String chars1 = "abcdefghijklmnopqrstuvwxyz";
	private static final String chars2 = "1234567890";
	private static final String chars3 = " '°.,:;-_?!+-*/()[]{}%$#";
	
	private static final String charSet = chars1+chars1.toUpperCase()+chars2+chars3;
	private static final String dir = "res/font/map/";
	
	public static void create(String name, Font font, int xPos, int yPos, int rows, boolean blacked) throws Exception{
		BufferedImage ima = ImageIO.read(new File(dir+"tex.png"));
		File outputfile = new File(dir+"tex_bu.png");
		ImageIO.write(ima, "png", outputfile);
		
		FileWriter fr = new FileWriter(new File(dir+name+".texFnt"));
		PrintWriter wr = new PrintWriter(fr);
		
		Graphics2D g = ima.createGraphics();
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.white);
		FontMetrics fm = g.getFontMetrics();
		
		int maxGlyphSizeX = 0;
		int maxGlyphSizeY = font.getSize()+5;
		
		for (int i = 0; i < charSet.length(); i++) {
			int x = fm.stringWidth(""+charSet.charAt(i));
			if(maxGlyphSizeX < x)maxGlyphSizeX = x;
		}
		maxGlyphSizeX+=3;
		
		final int rowBreak = charSet.length()/rows;
		int coX = 0;
		int coY = 1;
		
		int descent = fm.getDescent()+2;
		
		wr.println("Hgh:"+fm.getAscent());
		wr.println("Line:"+(maxGlyphSizeY-descent));
		
		for (int i = 0; i < charSet.length(); i++) {
			if(blacked){
				g.setColor(Color.black);
				for (int k1 = -1; k1 < 2; k1++) {
					for (int k2 = -1; k2 < 2; k2++) {
						g.drawString(""+charSet.charAt(i), coX*maxGlyphSizeX+xPos+1+k1, k2+coY*maxGlyphSizeY+yPos-descent);
					}
				}
				g.setColor(Color.white);
			}
			g.drawString(""+charSet.charAt(i), coX*maxGlyphSizeX+xPos+1, coY*maxGlyphSizeY+yPos-descent);
			
			wr.println("Char:"+charSet.charAt(i)+" "+(coX*maxGlyphSizeX+xPos)+" "+
					((coY-1)*maxGlyphSizeY+yPos)+" "+(maxGlyphSizeX-1)+" "+(maxGlyphSizeY-1)+" "+fm.charWidth(charSet.charAt(i)));
			
//			for (int j = 0; j < charSet.length(); j++) {
//				int w = fm.stringWidth(""+charSet.charAt(i)+charSet.charAt(j));
//				w -= fm.stringWidth(""+charSet.charAt(j));
//				wr.println("Dis:"+charSet.charAt(j)+" "+w);
//			}
			
			coX++;
			if(coX >= rowBreak){
				coY++;
				coX = 0;
			}
		}
		
		g.setColor(Color.red);
		g.drawRect(xPos-1, yPos-1, maxGlyphSizeX*rowBreak, maxGlyphSizeY*(rows+1));
		System.out.println((maxGlyphSizeX*rowBreak+xPos)+" "+(maxGlyphSizeY*(rows+1)+yPos));
		
		outputfile = new File(dir+"tex.png");
		ImageIO.write(ima, "png", outputfile);
		wr.close();
	}
	
	public static void main(String[] args) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeMono.ttf"));
			//font = null;//TODO removeTrap
			font = font.deriveFont(18f);
			create("MONO_18", font, 60, 960, 2, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
