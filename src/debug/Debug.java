package debug;

import term.TermColors;
import term.VisualisedTerminal;

public class Debug extends TermColors{
	
	private static LogSaver log;
	public static VisualisedTerminal term;
	
	public static void init(){
		log = new LogSaver();
	}
	
	public static void print(String s){
		if(term != null) term.print(s, TEXT);
		log.print(s, TEXT);
	}
	
	public static void print(String s, int c){
		if(term != null) term.print(s, c);
		log.print(s, c);
	}
	
	public static void println(String s){
		if(term != null) term.println(s);
		log.println(s);
	}
	
	public static void println(String s, int c){
		if(term != null) term.println(s, c);
		log.println(s, c);
	}
	
	public static void printException(Exception e){
		//TODO 
		print("*Error: "+e.getMessage(), ERROR);
		log.logError(e);
		e.printStackTrace();
	}
	
	public static void z_setShutdownDebug(PerformanceMonitor p1, PerformanceMonitor p2, PerformanceMonitor p3){
		log.setShutdownData(p1, p2, p3);
	}
}
