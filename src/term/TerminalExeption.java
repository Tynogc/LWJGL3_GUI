package term;

public class TerminalExeption extends Exception{
	
	private static final long serialVersionUID = 483758032225678l;
	
	public TerminalExeption(String couse){
		super(couse);
	}

	@Override
	public String toString() {
		return "TerminalExeption "+getMessage()+" "+getLocalizedMessage();
	}
}
