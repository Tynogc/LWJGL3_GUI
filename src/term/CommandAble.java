package term;

public interface CommandAble {

	/**
	 * Works the given String
	 * @param s Command
	 * @param answer should be a visualized Terminal or a gateway
	 * @throws TerminalExeption
	 */
	public void exec(String s, TermPrint answer) throws TerminalExeption;
	
	public String getToolTip(String s);
	
	public String[] getChoosing(String s);
}
