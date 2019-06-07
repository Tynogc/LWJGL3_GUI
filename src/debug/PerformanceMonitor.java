package debug;

import menu.FontRenderer;
import gui.SpriteBatch;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import main.graphics.Texture;
import main.graphics.TextureRegion;
import main.graphics.util.Color;

/**
 * The Performance-Monitor is handled as follows:<br>
 * Call start() at the start of a loop.<br>
 * Call mark(String location) at any given point you want to mark. The location-Argument must be unique!
 * <br>
 * Important: the order of mark-Calls with their location-Identifiers should never change!
 * 
 * @author Sven T. Schneider
 */
public class PerformanceMonitor {
	
	private final String name;
	
	protected List<Mark> marks;
	
	//Counts the loops
	protected long loop;
	
	private TextureRegion white;
	
	private long time;
	
	private int longestString;
	
	public static PerformanceMonitor timeSimple;
	public static PerformanceMonitor timeCplx;
	public static PerformanceM_GPU timeGpu;
	
	private static final Color[] colors = new Color[]{
			new Color(0,162,255), new Color(255,160,40), new Color(11,255,157), new Color(128,0,255),
			new Color(128,255,0), new Color(255,255,5), new Color(0,255,255), new Color(255,30,30)
	};
	
	public static void init(){
		timeSimple = new PerformanceMonitor("Timing Simple");
		timeCplx = new PerformanceMonitor("Timing Complex");
		timeGpu = new PerformanceM_GPU("CPU GPU");
		Debug.z_setShutdownDebug(timeSimple, timeCplx, timeGpu);
	}
	
	/**
	 * @param n The name of this Performance-Monitor, ideally the Thread-name.
	 */
	public PerformanceMonitor(String n){
		name = n;
		marks = new ArrayList<>();
		white = new TextureRegion(FontRenderer.tex, 100f, 100, 100, 100);
		time = System.nanoTime();
	}
	
	public void start(){
		if(marks.size() != 0)
			mark("Sleep");
		else
			time = System.nanoTime();
		
		loop++;
	}
	
	public void mark(String location){
		for (Mark mark : marks) {
			if(mark.name.matches(location)){
				long t = System.nanoTime();
				mark.mark(t-time);
				time = t;
				return;
			}
		}
		marks.add(new Mark(location));
		mark(location);
		if(location.length()>longestString)
			longestString = location.length();
	}
	
	public void draw(int x, int y, SpriteBatch sp, FontRenderer font){
		long l1 = 1;
		long l2 = 1;
		for (Mark mark : marks) {
			l1 += mark.t10;
			l2 += mark.t50;
		}
		
		font.render(sp, "Time-Marks: "+name, x, y);
		y+=20;
		
		int i = 0;
		for (Mark mark : marks) {
			mark.draw(x, y, sp, font, l1, l2, colors[i]);
			y+=30;
			
			i++;
			if(i>=colors.length) i = 0;
		}
	}
	
	public void print(PrintWriter p){
		long l1 = 1;
		long l2 = 1;
		for (Mark mark : marks) {
			l1 += mark.t10;
			l2 += mark.t500;
		}
		
		p.println("Total T500:"+milliseconds(l2)+" / T10:"+milliseconds(l1));
		int i = 0;
		for (Mark mark : marks) {
			mark.print(p, l1, l2, i++);
		}
	}
	
	/**
	 * Clears all Marks and previous recorded Times
	 */
	public void reset(){
		marks.clear();
		time = System.nanoTime();
	}
	
	protected class Mark{
		
		private final String name;
		
		private long t10;
		private long t50;
		private long t500;
		
		private long t10max;
		private long t500max;
		
		private Mark(String n){
			name = n;
		}
		
		protected void mark(long t){
			t10 = (t10*9l+t)/10l;
			t50 = (t50*49l+t)/50l;
			t500 = (t500*499l+t)/500l;
			
			//the first 100 loops are excluded from performance-Maximum
			if(t10>t10max && loop > 100)
				t10max = t10;
			if(t500>t500max && loop > 100)
				t500max = t500;
		}
		
		public void draw(int x, int y, SpriteBatch sp, FontRenderer font, long l1, long l2, Color c){
			double d1 = (double)t10/(double)l1;
			double d2 = (double)t50/(double)l2;
			String s = milliseconds(t50)+" "+percent(d2);
			font.render(sp, s, x+100, y);
			
			sp.setColor(Color.GRAY);
			sp.draw(white, x, y+10, 200, 2);
			sp.draw(white, x-2, y+6, 2, 5);
			sp.draw(white, x+99, y+4, 2, 7);
			sp.draw(white, x+200, y+4, 2, 7);
			sp.setColor(c);
			sp.draw(white, x, y+5, (float)d1*200, 3);
			font.render(sp, name+":", x, y);
			sp.setColor(Color.WHITE);
		}
		
		public void print(PrintWriter p, long l1, long l2, int i){
			p.println(uniformLength(name)+" ["+i+"]");
			double d1 = (double)t10/(double)l1;
			double d2 = (double)t500/(double)l2;
			p.println("   T10:"+milliseconds(t10)+" (MAX:"+milliseconds(t10max)+") "+bar(d1));
			p.println("  T500:"+milliseconds(t500)+" (MAX:"+milliseconds(t500max)+") "+bar(d2));
		}
	}
	
	private static String milliseconds(long t){
		t /= 1000;
		String s = (t/1000)+".";
		if(t/1000<10)s = " "+s;
		if(t<0)t*=-1;
		s+=""+((t/100)%10);
		s+=""+((t/10)%10);
		s+=""+(t%10);
		return s+"ms";
	}
	
	private static String percent(double t){
		String s = ""+(int)(t*100);
		if(s.length()<2)s = " "+s;
		s+="."+((int)(t*1000)%10);
		return s+"%";
	}
	
	private String uniformLength(String s){
		int u = longestString-s.length();
		s+=":";
		for (int i = 0; i < u; i++) {
			s+=" ";
		}
		return s;
	}
	
	private static String bar(double d){
		String s="#";
		for (int i = 0; i < 25; i++) {
			if(d>= 1.0/25.0 * i)s+="*";
			else s+="_";
		}
		return s+" "+percent(d);
	}
}
