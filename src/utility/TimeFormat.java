package utility;

import java.text.SimpleDateFormat;

public class TimeFormat {

	public boolean germanFormat;
	public boolean houre24;
	private static TimeFormat tf;
	
	public static String getTimeDay(long t, boolean seconds){
		if(tf == null)
			tf = new TimeFormat(true, true);
		
		return tf.getTH(t, seconds);
	}
	
	public static String getTimeDate(long t){
		if(tf == null)
			tf = new TimeFormat(true, true);
		
		return tf.getTD(t);
	}
	
	public static String getTimeAbsolut(long t, boolean seconds){
		if(tf == null)
			tf = new TimeFormat(true, true);
		
		return tf.getTD(t)+" "+tf.getTH(t, seconds);
	}
	
	public static void init(boolean germanFormat, boolean hour24){
		if(tf != null){
			if(germanFormat == tf.germanFormat && hour24 == tf.houre24)
				return;
		}
		tf = new TimeFormat(germanFormat, hour24);
	}
	
	private SimpleDateFormat date;
	private SimpleDateFormat hm;
	private SimpleDateFormat hms;
	
	private TimeFormat(boolean ger, boolean h24){
		germanFormat = ger;
		houre24 = h24;
		
		//TODO use formats
		date = new java.text.SimpleDateFormat("dd.MM.yyyy");
		hm = new java.text.SimpleDateFormat("HH:mm");
		hms = new java.text.SimpleDateFormat("HH:mm.ss");
	}
	
	private String getTD(long t){
		return date.format(new java.util.Date (t));
	}
	
	private String getTH(long t, boolean s){
		if(s)
			return hms.format(new java.util.Date (t));
		return hm.format(new java.util.Date (t));
	}
}
