package main.graphics;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class FrustumCullingFilter {
	
	private final Matrix4f prjViewMatrix;
	private final FrustumIntersection fis;

	public FrustumCullingFilter() {
		prjViewMatrix = new Matrix4f();
		fis = new FrustumIntersection();
	}

	public void updateFrustum(Matrix4f projMatrix, Matrix4f translate) {
		prjViewMatrix.set(projMatrix);
		prjViewMatrix.mul(translate);
		fis.set(prjViewMatrix);
	}
	
	public boolean insideFrustum(Vector4f v, float boundingRadius) {
		return insideFrustum(v.x, v.y, v.z, v.w, boundingRadius);
	}

	private boolean insideFrustum(float x0, float y0, float z0, float w0, float boundingRadius) {
		return fis.testSphere(x0, y0, z0, boundingRadius);
	}

}
