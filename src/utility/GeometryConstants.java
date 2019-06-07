package utility;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import main.graphics.obj.GenericVertexData;

public class GeometryConstants {

	public static final Matrix4f identity = new Matrix4f();

	public static final Vector3f zero = new Vector3f(0, 0, 0);
	public static final Vector3f xAxis = new Vector3f(1, 0, 0);
	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	public static final Vector3f zAxis = new Vector3f(0, 0, 1);
	
	public enum FACING{
		UP, DOWN, LEFT, RIGHT, FRONT, BACK
	};
	
	public static void createFace(GenericVertexData gvd, Vector3f normal, Vector3f tangent, Vector3f biTangent, Vector3f origin){
		createFace(gvd, normal, tangent, biTangent, 0, 0, 0, origin);
	}
	
	public static void createFace(int v, int i, GenericVertexData gvd, Vector3f normal, Vector3f tangent, Vector3f biTangent, Vector3f origin){
		createFace(v, i, gvd, normal, tangent, biTangent, 0, 0, 0, origin);
	}
	
	public static void createFace(GenericVertexData gvd, Vector3f normal, Vector3f tangent, Vector3f biTangent,
			float tu, float tv, float ts, Vector3f origin){
		createFace(0, 0, gvd, normal, tangent, biTangent, tu, tv, ts, origin);
	}
	
	public static void createFace(int v, int i, GenericVertexData gvd, Vector3f normal, Vector3f tangent, Vector3f biTangent,
			float tu, float tv, float ts, Vector3f origin){
		if(origin==null)origin = zero;
		
		float mx = tangent.length();
		float my = biTangent.length();
		float mu = Math.max(mx, my);
		mx /= mu;
		my /= mu;
		
		gvd.vertices[v] = new Vector3f(0).add(origin);
		gvd.vertices[1+v] = new Vector3f(0).add(tangent).add(origin);
		gvd.vertices[3+v] = new Vector3f(0).add(biTangent).add(origin);
		gvd.vertices[2+v] = new Vector3f(0).add(tangent).add(biTangent).add(origin);
		gvd.normals[v] = gvd.normals[1+v] = gvd.normals[2+v] = gvd.normals[3+v] = new Vector3f(normal).normalize();
		//gvd.tangents[0] = gvd.tangents[1] = gvd.tangents[2] = gvd.tangents[3] = new Vector3f(biTangent).normalize();
		//gvd.biTangents[0] = gvd.biTangents[1] = gvd.biTangents[2] = gvd.biTangents[3] = new Vector3f(tangent).normalize();
		gvd.texture[v] = new Vector2f(tu+ts*mx, tv+ts*my);
		gvd.texture[1+v] = new Vector2f(tu, tv+ts*my);
		gvd.texture[3+v] = new Vector2f(tu+ts*mx, tv);
		gvd.texture[2+v] = new Vector2f(tu, tv);
		
		gvd.indices[i] = 1+v;
		gvd.indices[i+1] = 2+v;
		gvd.indices[i+2] = v;
		gvd.indices[i+3] = 2+v;
		gvd.indices[i+4] = 3+v;
		gvd.indices[i+5] = v;
		
		gvd.processTangents(i, i+6);
	}
	
}
