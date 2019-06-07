package main.graphics.tec;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.joml.Matrix4f;

import gui.SpriteBatch;
import main.graphics.Camera;
import main.graphics.FrustumCullingFilter;
import main.graphics.Projection;
import main.graphics.obj.RenderChain;

/**
 * Can hold an arbitrary Rendering-Pipeline
 * @author Sven T. Schneider
 */
public class RenderPipeline {

	private List<RenderChain> chain;
	
	private Semaphore waitThreadMovement;
	private Semaphore waitThreadFrustum;
	private Semaphore waitThreadRestart;
	
	private FrustumCullingFilter fcf;
	private Matrix4f m;
	
	public static long timeMove;
	public static long timeFrust;
	
	public RenderPipeline(){
		chain = new LinkedList<>();
		
		m = new Matrix4f();
		fcf = new FrustumCullingFilter();
		
		waitThreadFrustum = new Semaphore(1);
		waitThreadMovement = new Semaphore(1);
		waitThreadRestart = new Semaphore(2);
		
		new Thread("Render-Update"){
			public void run() {
				while (true) {
					updateMovement();
				}
			};
		}.start();
		
		new Thread("Render-Frustum"){
			public void run() {
				while (true) {
					updateFrustum();
				}
			};
		}.start();
	}
	
	public List<RenderChain> getChain(){
		return chain;
	}
	
	/**
	 * Starts Sub-Methods, like Frustum-Culling and movement. This Methode can only be called once
	 * all Movement-Modifications are made!
	 */
	public void prepareRendering(Camera c, Projection p){
		waitThreadRestart.acquireUninterruptibly(2);
		waitThreadRestart.release(2);
		
		fcf.updateFrustum(p.getProjectionOnly(), c.getViewMatrix(m));
		
		waitThreadFrustum.release();
		waitThreadMovement.release();
	}
	
	public void render(){
		for (RenderChain r : chain) {
			r.renderChain();
			r.renderingDone();
		}
	}
	
	private void updateMovement(){
		waitThreadMovement.acquireUninterruptibly();
		waitThreadRestart.acquireUninterruptibly();
		long t = System.nanoTime();
		for (RenderChain r : chain) {
			r.update();
		}
		timeMove = System.nanoTime()-t + timeMove*49;
		timeMove /= 50;
		waitThreadRestart.release();
	}
	
	private void updateFrustum(){
		waitThreadFrustum.acquireUninterruptibly();
		waitThreadRestart.acquireUninterruptibly();
		long t = System.nanoTime();
		for (RenderChain r : chain) {
			r.checkFrustum(fcf);
		}
		timeFrust = System.nanoTime()-t + timeFrust*49;
		timeFrust /= 50;
		waitThreadRestart.release();
	}
	
	public void render2dProjection(Matrix4f cam, SpriteBatch sp){
		for (RenderChain r : chain) {
			r.render2Dprojections(cam, sp);
		}
	}
}
