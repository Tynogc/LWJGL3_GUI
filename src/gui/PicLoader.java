package gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import main.graphics.Texture;
import main.graphics.TextureRegion;

public class PicLoader {

	public final Texture atlas;
	
	private List<SingleImage> imas;
	
	protected boolean printWarning = true;
	
	class SingleImage{
		public final String name;
		public final TextureRegion tex;
		public SingleImage(String n, TextureRegion t){
			name = n;
			tex = t;
		}
	}
	
	public static PicLoader pic;
	
	public PicLoader(String dir) throws Exception{
		atlas = new Texture(utility.ResourceLoader.loadResourceURL(dir+".png"));
		
		imas = new ArrayList<>();
		
		FileReader fr = new FileReader(dir+".map");
		BufferedReader br = new BufferedReader(fr);
		
		String s;
		while ((s = br.readLine()) != null) {
			if(s.length()<3)
				continue;
			if(s.startsWith("//"))
				continue;
			s = s.replace(',', ' ');
			String[] st = s.split(" ");
			TextureRegion tr = new TextureRegion(atlas, Integer.parseInt(st[1]), Integer.parseInt(st[2]),
					Integer.parseInt(st[3]), Integer.parseInt(st[4]));
			imas.add(new SingleImage(st[0], tr));
		}
		
		br.close();
	}
	
	public TextureRegion getImage(String s){
		for (SingleImage si : imas) {
			if(si.name.matches(s))
				return si.tex;
		}
		if(printWarning)
			debug.Debug.println("* PicLoader: Image not found "+s, debug.Debug.WARN);
		return imas.get(0).tex;
	}
}
