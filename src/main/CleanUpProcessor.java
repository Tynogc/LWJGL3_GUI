package main;

import java.util.ArrayList;
import java.util.List;

public class CleanUpProcessor {

	private List<CleanUpAction> list;
	
	private static CleanUpProcessor me;
	
	private boolean isCleaning = false;
	
	public CleanUpProcessor(){
		me = this;
		list = new ArrayList<>(500);
	}
	
	public void cleanAll(){
		isCleaning = true;
		for (CleanUpAction c : list) {
			c.clean();
		}
		isCleaning = false;
		list.clear();
	}
	
	public static void addCleanJob(CleanUpAction ca){
		if(me.isCleaning) return;
		me.list.add(ca);
	}
	
	public static void removeCleanJob(CleanUpAction ca){
		if(me.isCleaning) return;
		me.list.remove(ca);
	}
	
	public interface CleanUpAction{
		public void clean();
	}
}
