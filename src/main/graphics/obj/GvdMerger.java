package main.graphics.obj;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import utility.Count;

public class GvdMerger {

	public static void merge(GenericVertexData in, GenericVertexData out, Count countVerticies, Count countIndicies){
		merge(in, out, countVerticies, countIndicies, null, null, null);
	}
	
	public static void merge(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector3f move){
		merge(in, out, countVerticies, countIndicies, move, null, null);
	}
	
	public static void merge(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Matrix4f move){
		merge(in, out, countVerticies, countIndicies, null, move, null);
	}
	
	public static void merge(GenericVertexData in, GenericVertexData out, Count countVerticies,
			Count countIndicies, Vector2f texOff){
		merge(in, out, countVerticies, countIndicies, null, null, texOff);
	}
	
	public static void merge(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector2f texOff,Vector3f move){
		merge(in, out, countVerticies, countIndicies, move, null, texOff);
	}
	
	public static void merge(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies,Vector2f texOff, Matrix4f move){
		merge(in, out, countVerticies, countIndicies, null, move, texOff);
	}
	
	private static void merge(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector3f v, Matrix4f m, Vector2f texOffset){
		
		if(in.indexMode && out.indexMode)mergeIndexIndex(in, out, countVerticies, countIndicies, v, m, texOffset);
		else if(in.indexMode && !out.indexMode)mergeIndexSequ(in, out, countVerticies, countIndicies, v, m, texOffset);
		else mergeSequ(in, out, countVerticies, countIndicies, v, m, texOffset);
	}
	
	private static void mergeSequ(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector3f v, Matrix4f m, Vector2f texOffset){
		
		for (int i = 0; i < in.vertices.length; i++) {
			out.vertices[countVerticies.c] = mv(in.vertices[i], v, m, 1);
			out.normals[countVerticies.c] = mv(in.normals[i], null, m, 0);
			out.tangents[countVerticies.c] = mv(in.tangents[i], null, m, 0);
			out.biTangents[countVerticies.c] = mv(in.biTangents[i], null, m, 0);
			out.texture[countVerticies.c] = to(in.texture[i], texOffset);
			
			if(out.indexMode){
				out.indices[countIndicies.c++] = countVerticies.c;
			}
			countVerticies.c++;
		}
	}
	
	private static void mergeIndexIndex(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector3f v, Matrix4f m, Vector2f texOffset){
		
		int vertexOffset = countVerticies.c;
		for (int i = 0; i < in.vertices.length; i++) {
			out.vertices[countVerticies.c] = mv(in.vertices[i], v, m, 1);
			out.normals[countVerticies.c] = mv(in.normals[i], null, m, 0);
			out.tangents[countVerticies.c] = mv(in.tangents[i], null, m, 0);
			out.biTangents[countVerticies.c] = mv(in.biTangents[i], null, m, 0);
			out.texture[countVerticies.c] = to(in.texture[i], texOffset);
			countVerticies.c++;
		}
		
		for (int i = 0; i < in.indices.length; i++) {
			out.indices[i+countIndicies.c] = in.indices[i]+vertexOffset;
		}
		countIndicies.c += in.indices.length;
	}
	
	private static void mergeIndexSequ(GenericVertexData in, GenericVertexData out,
			Count countVerticies, Count countIndicies, Vector3f v, Matrix4f m, Vector2f texOffset){
		
		for (int i = 0; i < in.indices.length; i++) {
			int u = in.indices[i];
			out.vertices[countVerticies.c] = mv(in.vertices[u], v, m, 1);
			out.normals[countVerticies.c] = mv(in.normals[u], null, m, 0);
			out.tangents[countVerticies.c] = mv(in.tangents[u], null, m, 0);
			out.biTangents[countVerticies.c] = mv(in.biTangents[u], null, m, 0);
			out.texture[countVerticies.c] = to(in.texture[u], texOffset);
			
			countVerticies.c++;
		}
	}
	
	private static Vector4f w = new Vector4f();
	private static synchronized Vector3f mv(Vector3f in, Vector3f v, Matrix4f m, int nu){
		if(m == null && v == null)return in;
		if(m != null){
			w.set(in, nu);
			m.transform(w);
			return new Vector3f(w.x, w.y, w.z);
		}
		return new Vector3f(in).add(v);
	}
	
	private static Vector2f to(Vector2f in, Vector2f t){
		if(t == null)return in;
		return new Vector2f(in).add(t);
	}
}
