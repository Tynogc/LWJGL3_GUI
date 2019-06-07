package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LoadingPerk {

	public static String[][] load(String dir){
		return load(new File(dir));
	}
	
	public static String[][] load(File dir){
		List<String[]> ret = new ArrayList<>();
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr);
			
			String[] work = new String[2];
			work[0] = "";
			work[1] = "";
			int wPos = 0;
			
			String s;
			while ((s = br.readLine()) != null) {
				int slash = s.indexOf("//");
				if(slash >= 0)
					s = s.substring(0, slash);
				
				int open;
				int close;
				do{
					open = s.indexOf('{');
					close = s.indexOf('}');
					
					if(open>=0 && (open<close ||  close<0)){
						work[wPos] += s.substring(0, open).trim();
						wPos = 1;
						
						if(open+1>=s.length())s = "";
						else s = s.substring(open+1);
						
						close = s.indexOf('}');
					}
					
					if(close>=0){
						work[wPos] += s.substring(0, close).trim();
						ret.add(work);
						wPos = 0;
						work = new String[2];
						work[0] = "";
						work[1] = "";
						
						if(close+1>=s.length())s = "";
						else s = s.substring(close+1);
					}
					
				}while(open >= 0 || close >= 0);
				
				work[wPos] += s.trim()+" ";
			}
			br.close();
			
			String[][] rt = new String[ret.size()][2];
			int i = 0;
			for (String[] st : ret) {
				st[0] = st[0].trim();
				st[1] = st[1].trim();
				rt[i] = st;
				i++;
			}
			return rt;
		} catch (Exception e) {
			debug.Debug.printException(e);
		}
		return new String[0][0];
	}
	
	public static String[][] splitSegments(String s){
		String[] st1 = s.split(";");
		String[][] st2 = new String[st1.length][0];
		
		for (int i = 0; i < st2.length; i++) {
			st2[i] = st1[i].trim().split(",");
			for (int j = 0; j < st2[i].length; j++) {
				st2[i][j] = st2[i][j].trim();
			}
		}
		String[][] st3 = new String[st1.length][0];
		for (int i = 0; i < st2.length; i++) {
			String[] sq = st2[i][0].split("=");
			st3[i] = new String[st2[i].length+1];
			st3[i][0] = sq[0].trim();
			if(sq.length>1)
			st3[i][1] = sq[1].trim();
			for (int j = 1; j < st2[i].length; j++) {
				st3[i][j+1] = st2[i][j].trim();
			}
		}
		return st3;
	}
}
