package main;

import java.util.concurrent.Semaphore;

public class Timer extends Thread{

	public static long currentTime;
	public static int timePassed = 1000;
	 
	public static int fps;
	
	private Semaphore sema;
	
	private long sumTime;
	private int framesInTime;
	
	public Timer(){
		sema = new Semaphore(1);
		
		start();
	}
	
	public void run(){
		long lt = System.nanoTime();
		
		while (true) {
			if(sema.availablePermits()<=0)
				sema.release();
		}
	}
	
	public void waitNextFrame(){
		sema.acquireUninterruptibly();
		framesInTime++;
		
		long t = System.currentTimeMillis();
		timePassed = (int)(t-currentTime);
		if(timePassed>1000)timePassed = 1000;
		if(timePassed<0)timePassed = 1000;
		currentTime = t;
		sumTime += timePassed;
		
		if(sumTime < 0)sumTime = 0;
		
		if(sumTime > 500){
			fps = framesInTime*2;
			sumTime -= 500;
			if(sumTime > 2000) sumTime = 2000;
			
			framesInTime = 0;
		}
	}
}
