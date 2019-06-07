package term;

import term.BasicCommand;
import term.TermPrint;
import term.Terminal;

public class TerminalControl {

	private Terminal test;
	
	public Terminal start;
	
	public TerminalControl(){
		term.BasicCommand bc = new BasicCommand("ToolTip123", "otcc", new String[]{"A", "B"}) {
			@Override
			protected void cmd(String[] s, TermPrint answer) {
				debug.Debug.println(s[0]);
			}
		};
		test = new Terminal(null, null);
		test.setChoosable(new String[]{"test", "test2", "mtest", "otcc"}, new term.CommandAble[]{test, test, test, bc});
		
		///////////////////////
		
		start = new Terminal(new String[]{"me","/"}, new Terminal[]{test, test});
	}
}
