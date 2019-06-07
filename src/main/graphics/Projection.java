package main.graphics;

import org.joml.Matrix4f;

public class Projection {

	private Matrix4f projectionMatrix;
	
	private static final float Z_NEAR = 0.01f;
	private static final float Z_FAR = 900f;
	
	private float aspect;
	
	private float fov = 60;
	
	private Matrix4f mat;
	
	public Projection(){
		aspect = 1.0f;
		mat = new Matrix4f();
		updateVM();
	}
	
	public void setFOV(float f){
		fov = f;
		updateVM();
	}
	
	public void setAspect(int width, int height){
		aspect = (float)width/height;
		updateVM();
	}
	
	public void updateVM(){
		projectionMatrix = new Matrix4f().perspective((float)Math.toRadians(fov), aspect,
				Z_NEAR, Z_FAR);
	}
	
	public Matrix4f getProjectionOnly(){
		return projectionMatrix;
	}
	
	public Matrix4f getMatrix(Camera c, Matrix4f m){
		return m.set(projectionMatrix).mul(c.getViewMatrix(mat));
	}
}
