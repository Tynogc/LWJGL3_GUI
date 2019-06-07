package menu;

public class Fonts {

	public static FontRenderer fontMono18;
	public static FontRenderer fontMono14;
	public static FontRenderer font14;
	public static FontRenderer fontBold14;
	
	public static void init(){
		fontMono18 = FontRenderer.getFont("MONO_18");
		fontMono14 = FontRenderer.getFont("MONO_14");
		font14 = FontRenderer.getFont("SANS_14");
		fontBold14 = FontRenderer.getFont("SANS_BOLD_14");
	}
}
