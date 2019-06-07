package term;

public class Terminal implements CommandAble{

	public String[] commands;
	public CommandAble[] terminals;
	
	public Terminal(String[] c, CommandAble[] t){
		setChoosable(c, t);
	}
	
	public void setChoosable(String[] c, CommandAble[] t){
		if(c==null)return;
		
		int u = 0;
		for (int i = 0; i < t.length; i++) {
			if(c[i] != null && t[i] != null)u++;
		}
		
		if(u == 0)return;
		
		if(u != c.length){
			commands = new String[u];
			terminals = new CommandAble[u];
			u = 0;
			for (int i = 0; i < t.length; i++) {
				if(c[i] != null && t[i] != null){
					commands[u] = c[i];
					terminals[u] = t[i];
					u++;
				}
			}
			c = commands;
			t = terminals;
		}else{
			commands = c;
			terminals = t;
		}
		
		for (int i = 0; i < c.length; i++) {
			if(c[i].length()>0 && t[i] instanceof Terminal)
				if(c[i].charAt(c[i].length()-1) != '.')
					c[i] += ".";
			if(c[i].length()>0 && t[i] instanceof BasicCommand)
				if(c[i].charAt(c[i].length()-1) != ' ')
					c[i] += " ";
		}
	}

	@Override
	public void exec(String s, TermPrint answer) throws TerminalExeption {
		for (int i = 0; i < commands.length; i++) {
			if(s.toLowerCase().startsWith(commands[i].toLowerCase())){
				terminals[i].exec(s.substring(commands[i].length()), answer);
				return;
			}
		}
		answer.println("Can't resolve: "+s, TermColors.COMERR);
	}

	@Override
	public String getToolTip(String s) {
		s = s.toLowerCase();
		for (int i = 0; i < commands.length; i++) {
			if(s.startsWith(commands[i].toLowerCase())){
				return terminals[i].getToolTip(s.substring(commands[i].length()));
			}
		}
		return null;
	}

	@Override
	public String[] getChoosing(String s) {
		s = s.toLowerCase();
		for (int i = 0; i < commands.length; i++) {
			if(s.startsWith(commands[i].toLowerCase())){
				return terminals[i].getChoosing(s.substring(commands[i].length()));
			}
		}
		int u = 0;
		for (int i = 0; i < commands.length; i++) {
			if(commands[i].toLowerCase().startsWith(s))
				u++;
		}
		String[] k = new String[u];
		u = 0;
		for (int i = 0; i < commands.length; i++) {
			if(commands[i].toLowerCase().startsWith(s)){
				k[u] = commands[i];
				u++;
			}
		}
		return k;
	}
}
