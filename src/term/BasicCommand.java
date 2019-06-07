package term;

public abstract class BasicCommand implements CommandAble{

	public String toolTip;
	
	public String[] clicks;
	
	public BasicCommand(String tt) {
		toolTip = tt;
	}
	
	public BasicCommand(String tt, String pre, String[] c) {
		toolTip = tt;
		clicks = c;
		for (int i = 0; i < c.length; i++) {
			c[i] = pre+" "+c[i];
		}
	}
	
	@Override
	public void exec(String s, TermPrint answer) throws TerminalExeption {
		cmd(s.trim().split(" "), answer);
	}

	@Override
	public String getToolTip(String s) {
		return toolTip;
	}

	@Override
	public String[] getChoosing(String s) {
		if(clicks == null)
		return null;
		
		int u = 0;
		for (int i = 0; i < clicks.length; i++) {
			if(clicks[i].toLowerCase().startsWith(s))
				u++;
		}
		String[] st = new String[u];
		u = 0;
		for (int i = 0; i < clicks.length; i++) {
			if(clicks[i].toLowerCase().startsWith(s)){
				st[u] = clicks[i];
				u++;
				if(u>=st.length)
					break;
			}
		}
		return st;
	}

	protected abstract void cmd(String[] s, TermPrint answer);
	
}
