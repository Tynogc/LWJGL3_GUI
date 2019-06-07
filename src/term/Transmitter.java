package term;

public class Transmitter implements TermPrint{

	private final String bevore;
	private final String after;
	private final TermPrint pr;
	
	public static final String HEADER = "-_-Q-_-";
	
	public Transmitter(String b, TermPrint p, String a) {
		if(b == null)
			bevore = "";
		else if(b.length() == 0)
			bevore = "";
		else
			bevore = b+HEADER;
		pr = p;
		if(a != null)
			after = a;
		else
			after = "";
	}
	
	@Override
	public void print(String s) {
		pr.print(bevore+s+after);
	}

	@Override
	public void println(String s) {
		pr.println(bevore+s+after);
	}

	@Override
	public void print(String s, int color) {
		pr.print(bevore+s+after, color);
	}

	@Override
	public void println(String s, int color) {
		pr.println(bevore+s+after, color);
	}

}
