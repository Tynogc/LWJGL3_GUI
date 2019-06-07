package main.graphics.obj;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GenericVertexData {

	public Vector3f[] vertices;
	public Vector3f[] normals;
	public Vector3f[] tangents;
	public Vector3f[] biTangents;
	
	public Vector2f[] texture;
	
	public int[] indices;
	
	public final boolean indexMode;
	
	public Vector3f position;
	public Vector3f minusSize;
	public Vector3f plusSize;
	public float size;
	
	public GenericVertexData(boolean idxMode){
		indexMode = idxMode;
	}
	
	public GenericVertexData(boolean idxMode, int vSize, int iSize){
		indexMode = idxMode;
		
		vertices = new Vector3f[vSize];
		normals = new Vector3f[vSize];
		tangents = new Vector3f[vSize];
		biTangents = new Vector3f[vSize];
		texture = new Vector2f[vSize];
		
		if(indexMode) indices = new int[iSize];
	}
	
	/**
	 * Pre-Compiles this GVD-Object into the Attribute-Data-Objects given
	 * @param v Vertex-Attribute (v3)
	 * @param n Normal-Attribute (v3)
	 * @param tx Texture-Attribute (v2)
	 */
	public void compile(AttributeData v, AttributeData n, AttributeData tx){
		v.setData(vertices);
		n.setData(normals);
		tx.setData(texture);
	}
	
	public Mesh compileToLandscape(){
		if(indexMode)
			return area.LandscapeShader.generateMeshIdx(vertices, normals, texture, tangents, biTangents, indices);
		return area.LandscapeShader.generateMeshSeq(vertices, normals, texture, tangents, biTangents);
	}
	
	public void processTangents(){
		if(tangents == null)
			tangents = new Vector3f[vertices.length];
		if(biTangents == null)
			biTangents = new Vector3f[vertices.length];
		utility.MeshUtility.processTangents(this);
	}
	
	public void processTangents(int iStart, int iStop){
		if(tangents == null)
			tangents = new Vector3f[vertices.length];
		if(biTangents == null)
			biTangents = new Vector3f[vertices.length];
		utility.MeshUtility.processTangents(this, iStart, iStop);
	}
	
	public void processSize(){
		Vector3f min = new Vector3f(100000f);
		Vector3f max = new Vector3f(-100000f);
		for (int i = 0; i < vertices.length; i++) {
			Vector3f f = vertices[i];
			
			min.x = Math.min(min.x, f.x);
			min.y = Math.min(min.y, f.y);
			min.z = Math.min(min.z, f.z);
			max.x = Math.max(max.x, f.x);
			max.y = Math.max(max.y, f.y);
			max.z = Math.max(max.z, f.z);
		}
		position = new Vector3f(min);
		size = min.distance(max)/2;
		position.add(max).div(2);
		
		minusSize = min;
		plusSize = max;
	}
	
	public boolean dosePreIntersect(Vector3f origin, float maxDist){
		return org.joml.Intersectionf.testAabSphere(minusSize, plusSize, origin, maxDist * maxDist);
	}
	
	public float checkColision(Vector3f origin, Vector3f dir, float maxDist) {
		if (!dosePreIntersect(origin, maxDist))
			return -100;
		
		if (!org.joml.Intersectionf.testRayAab(origin, dir, minusSize, plusSize))
			return -100;

		return checkColisionWithoutPreCheck(origin, dir);
	}
	
	public float checkColisionWithoutPreCheck(Vector3f origin, Vector3f dir){
		if(indexMode) return checkColisionIndexMode(origin, dir);
		return checkColisionSequMode(origin, dir);
	}
	
	private float checkColisionSequMode(Vector3f origin, Vector3f dir){
		float low = 1e10f;
		for (int i = 0; i < vertices.length; i+=3) {
			float t = org.joml.Intersectionf.intersectRayTriangle(origin, dir,
					vertices[i], vertices[i+1], vertices[i+2], 1E-5f);

			if (t < 0f)
				continue;
			if (t < low)
				low = t;
		}
		if (low > 1e9f)
			return -100;
		return low;
	}
	
	private float checkColisionIndexMode(Vector3f origin, Vector3f dir){
		float low = 1e10f;
		for (int i = 0; i < indices.length; i+=3) {
			float t = org.joml.Intersectionf.intersectRayTriangle(origin, dir,
					vertices[indices[i]], vertices[indices[i+1]], vertices[indices[i+2]], 1E-5f);

			if (t < 0f)
				continue;
			if (t < low)
				low = t;
		}
		if (low > 1e9f)
			return -100;
		return low;
	}
	
	public void move(Vector3f m){
		boolean[] c = new boolean[vertices.length];
		int i = 0;
		for (Vector3f v : vertices) {
			if(!c[i])
				v.add(m);
			c[i++] = true;
		}
		processSize();
	}
	
	public void scale(Vector3f m){
		for (Vector3f v : vertices) {
			v.mul(m);
		}
		processSize();
	}
	
	public void move(Matrix4f m){
		Vector4f t = new Vector4f();
		boolean[] c = new boolean[vertices.length];
		int i = 0;
		for (Vector3f v : vertices) {
			if(!c[i]){
				t.set(v, 1);
				m.transform(t);
				v.set(t.x, t.y, t.z);
			}
			c[i++] = true;
		}
		for (int j = 0; j < c.length; j++) c[j] = false;
		i = 0;
		for (Vector3f v : normals) {
			if(!c[i]){
				t.set(v, 0);
				m.transform(t);
				v.set(t.x, t.y, t.z);
			}
			c[i++] = true;
		}
		for (int j = 0; j < c.length; j++) c[j] = false;
		i = 0;
		for (Vector3f v : tangents) {
			if(!c[i]){
				t.set(v, 0);
				m.transform(t);
				v.set(t.x, t.y, t.z);
			}
			c[i++] = true;
		}
		for (int j = 0; j < c.length; j++) c[j] = false;
		i = 0;
		for (Vector3f v : biTangents) {
			if(!c[i]){
				t.set(v, 0);
				m.transform(t);
				v.set(t.x, t.y, t.z);
			}
			c[i++] = true;
		}
		for (int j = 0; j < c.length; j++) c[j] = false;
		
		processSize();
	}
	
	@Override
	public GenericVertexData clone(){
		GenericVertexData g = new GenericVertexData(indexMode);
		
		for (int i = 0; i < vertices.length; i++) {
			g.vertices[i] = new Vector3f(vertices[i]);
			g.normals[i] = new Vector3f(normals[i]);
			g.biTangents[i] = new Vector3f(biTangents[i]);
			g.tangents[i] = new Vector3f(tangents[i]);
			g.texture[i] = new Vector2f(texture[i]);
		}
		
		if(indexMode) g.indices = Arrays.copyOf(indices, indices.length);
		
		return g;
	}
}
