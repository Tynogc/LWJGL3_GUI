package debug;

public class PerformanceM_GPU extends PerformanceMonitor{

	private long t_CPU;
	private long t_BUS;
	private long t_GPU;
	private long t_SLE;
	
	private long time;
	
	public PerformanceM_GPU(String n) {
		super(n);
		mark("CPU");
		mark("BUS");
		mark("GPU");
		mark("Sync");
		
		time = System.nanoTime();
	}
	
	@Override
	public void start() {
		marks.get(0).mark(t_CPU);
		marks.get(1).mark(t_BUS);
		marks.get(2).mark(t_GPU);
		marks.get(3).mark(t_SLE);
		t_CPU = t_BUS = t_GPU = t_SLE = 0;
		
		loop++;
	}

	private long t;
	
	public void markSleep_done(){
		t = System.nanoTime();
		t_SLE += t-time;
		time = t;
	}
	
	public void markUNI_done(){
		t = System.nanoTime();
		t_BUS += t-time;
		time = t;
	}
	
	public void markCPU_done(){
		t = System.nanoTime();
		t_CPU += t-time;
		time = t;
	}
	
	public void markBUS_done(){
		t = System.nanoTime();
		t_BUS += t-time;
		time = t;
	}
	
	public void markGPU_done(){
		t = System.nanoTime();
		t_GPU += t-time;
		time = t;
	}
}
