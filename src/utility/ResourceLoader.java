package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class ResourceLoader {

	public static URL loadResourceURL(String f){
		URL url = ResourceLoader.class.getResource(f);
		if(url == null){
			try {
				File fi = new File(f);
				if(fi.exists())
					return fi.toURI().toURL();
			} catch (Exception e) {
				return null;
			}
		}
		return url;
	}
	
	public static String loadResourceString(String f){
		String s = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			String q;
			while ((q = br.readLine()) != null) {
				if(q.contains("###include#:")){
					int n = q.indexOf("###include#:")+"###include#:".length();
					q = q.substring(n).trim();
					s += loadResourceString(q);
				} else {
					s += q+"\n";
				}
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return s;
	}
}
