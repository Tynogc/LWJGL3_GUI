package main.graphics.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import main.graphics.FrustumCullingFilter;
import main.graphics.Render3D;
import main.graphics.Texture;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderChain {

	public final GenericVertexData data;
	public final Texture texure;
	private final Mesh dataRender;
	
	private Matrix4f movementRaw;
	private Matrix4f movement;
	private Matrix4f inverted;
	
	private boolean updateNeedet;
	
	private Semaphore waitForUpdate;
	private Semaphore waitForCulling;
	
	private List<RenderChain> next;
	
	private boolean frustumCulled = false;
	
	public List<ProjectionPoint> projectionPoints;
	
	public RenderChain(GenericVertexData d, Texture t) {
		data = d;
		if(data != null){
			dataRender = d.compileToLandscape();
		}else{
			dataRender = null;
		}
		texure = t;
		
		movementRaw = new Matrix4f();
		movement = new Matrix4f();
		inverted = new Matrix4f();
		
		next = new ArrayList<>();
		
		waitForCulling = new Semaphore(1);
		waitForUpdate = new Semaphore(1);
		waitForCulling.tryAcquire();
		waitForUpdate.tryAcquire();
	}
	
	public void setMovement(Vector3f tran, Vector3f rot){
		movementRaw.identity();
		movementRaw.translate(tran);
		if(rot.x != 0 || rot.y != 0 || rot.z != 0)
			movementRaw.rotate(rot.y, utility.GeometryConstants.yAxis)
			.rotate(rot.x, utility.GeometryConstants.xAxis)
			.rotate(rot.z, utility.GeometryConstants.zAxis);
		
		setUpdateNeedet(true);
	}
	
	public void setMovement(Matrix4f m) {
		movementRaw = m;
		setUpdateNeedet(true);
	}
	
	/**
	 * Call First, to update the Movement-Matrix
	 */
	public void update(){
		update(false, null);
	}
	
	protected void update(boolean forceUpdate, Matrix4f prev){
		if(forceUpdate || isUpdateNeedet()){
			if(prev != null) movement.set(prev).mul(movementRaw);
			else movement.set(movementRaw);
		}
		
		if(waitForUpdate.availablePermits() == 0)
			waitForUpdate.release();
		
		for (RenderChain r : next) {
			r.update(forceUpdate || isUpdateNeedet(), movement);
		}
		
		setUpdateNeedet(false);
	}
	
	private Vector4f fw = new Vector4f();
	
	/**
	 * Call Second, to update the Culled-State
	 */
	public void checkFrustum(FrustumCullingFilter fcf){
		waitForUpdate.acquireUninterruptibly();
		waitForUpdate.release();
		
		fw.set(data.position, 1);
		movement.transform(fw);
		frustumCulled = !fcf.insideFrustum(fw, data.size);
		
		if(waitForCulling.availablePermits() == 0)
			waitForCulling.release();
		
		for (RenderChain r : next) {
			r.checkFrustum(fcf);
		}
	}
	
	/**
	 * Call Third, for every consecutive rendering-pass
	 */
	public void renderChain(){
		waitForUpdate.acquireUninterruptibly();
		waitForUpdate.release();
		waitForCulling.acquireUninterruptibly();
		waitForCulling.release();
		
		if(dataRender != null && !frustumCulled){
			if(texure != null)Render3D.setCurrentTexture(texure);
			
			area.LandscapeShader.lss.setTransformation(movement);
			dataRender.render();
		}
		
		for (RenderChain r : next) {
			r.renderChain();
		}
	}
	
	/**
	 * Call Last, as soon as all rendering for that frame is done
	 */
	public void renderingDone(){
		waitForUpdate.tryAcquire();
		waitForCulling.tryAcquire();
		
		for (RenderChain r : next) {
			r.renderingDone();
		}
	}
	
	public void render2Dprojections(Matrix4f cam, gui.SpriteBatch sp){
		if(projectionPoints != null)
			for (ProjectionPoint p : projectionPoints) {
				p.project(cam, movement, sp);
			}
		
		for (RenderChain r : next) {
			r.render2Dprojections(cam, sp);
		}
	}

	private void setUpdateNeedet(boolean updateNeedet) {
		this.updateNeedet = updateNeedet;
	}

	public boolean isUpdateNeedet(){
		return updateNeedet;
	}

	private Vector4f w = new Vector4f();
	private Vector3f v1 = new Vector3f();
	private Vector3f v2 = new Vector3f();
	
	public float checkColision(Vector3f origin, Vector3f dir, float maxDist){
		if(data == null)return -100;
		
		w.set(origin, 1);
		inverted.transform(w);
		v1.set(w.x, w.y, w.z);
		
		w.set(dir, 0);
		inverted.transform(w);
		v2.set(w.x, w.y, w.z);
		
		//return data.checkColision(v1, v2, maxDist);
		return -1; //TODO
	}
}
