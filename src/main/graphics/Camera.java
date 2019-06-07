package main.graphics;

import org.joml.FrustumRayBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import utility.GeometryConstants;

public class Camera {

	public Vector3f pos;
	public Vector3f rot;
	public Vector3f rotationPos;
	
	private FrustumRayBuilder rayTrace;
	private Matrix4f mt;
	
	public Camera(){
		rotationPos = new Vector3f(0, 0, 0);
		pos = new Vector3f(0, 0, 0);
		rot = new Vector3f(0, 0, 0);
		
		rayTrace = new FrustumRayBuilder();
		mt = new Matrix4f();
	}
	
	public Matrix4f getPosition(){
		return new Matrix4f().translate(rotationPos.x, rotationPos.y, rotationPos.z);
	}
	
	public void setPosition(float x, float y, float z) {
		pos.x = x;
		pos.y = y;
		pos.z = z;
	}
	
	public void updateRayTrace(Projection p){
		rayTrace.origin(new Vector3f(pos));
		rayTrace.set(p.getMatrix(this, mt));
	}
	
	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		if (offsetZ != 0) {
			pos.x += (float) Math.sin(Math.toRadians(rot.y)) * -1.0f * offsetZ;
			pos.z += (float) Math.cos(Math.toRadians(rot.y)) * offsetZ;
		}
		if (offsetX != 0) {
			pos.x += (float) Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * offsetX;
			pos.z += (float) Math.cos(Math.toRadians(rot.y - 90)) * offsetX;
		}
		pos.y += offsetY;
	}
	
	public void setRotation(float x, float y, float z) {
		rot.x = x;
		rot.y = y;
		rot.z = z;
	}

	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		rot.x += offsetX;
		rot.y += offsetY;
		rot.z += offsetZ;
	}
	
	public Vector3f getRayTrace(float x, float y){
		return (Vector3f)rayTrace.dir(x, y, new Vector3f());
	}
	
	public Matrix4f getViewMatrix(Matrix4f m) {
		return getViewMatrix(pos, rot, m);
	}
	
	public static Matrix4f getViewMatrix(Vector3f pos, Vector3f rot, Matrix4f viewMatrix) {
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(rot.y), 0, 1, 0)
				.rotate((float) Math.toRadians(rot.x), 1, 0, 0)
				.rotate((float) Math.toRadians(rot.z), 0, 0, 1);
		
		if(pos != null)
			viewMatrix.translate(-pos.x, -pos.y, -pos.z);
		return viewMatrix;
	}
	
	private Vector3f vq = new Vector3f(); 
	public void orientAlongVector(Vector3f v){
		vq.set(v.x, v.y, 0);
		rot.z = (float)Math.toDegrees(vq.angle(GeometryConstants.yAxis));
		if(v.x<0)
			rot.z = 360-rot.z;
		rot.x = (float)Math.toDegrees(v.angle(GeometryConstants.zAxis))-180;
		rot.y = 0;
	}

}
