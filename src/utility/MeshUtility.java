package utility;

import org.joml.Vector2f;
import org.joml.Vector3f;

import main.graphics.obj.GenericVertexData;

public class MeshUtility {

	private static int muA;
	
	public static void processTangents(GenericVertexData gvd){
		if(gvd.indexMode) processTangentsIdx(gvd, 0, gvd.indices.length);
		else processTangentsSeq(gvd, 0, gvd.vertices.length);
	}
	
	public static void processTangents(GenericVertexData gvd, int iStart, int iStop){
		if(gvd.indexMode) processTangentsIdx(gvd, iStart, iStop);
		else processTangentsSeq(gvd, iStart, iStop);
	}
	
	private static void processTangentsSeq(GenericVertexData gvd, int iStart, int iStop){
		Vector2f[] tex = new Vector2f[3];
		Vector3f[] pos = new Vector3f[3];
		
		muA = 0;
		
		for (int i = iStart; i < iStop; i+=3) {
			for (int j = 0; j < 3; j++) {
				tex[j] = gvd.texture[i+j];
				pos[j] = gvd.vertices[i+j];
			}
			Vector3f t = getTangent(new Vector3f(), pos, tex);
			Vector3f bt = getBiTangent(new Vector3f(), pos, tex);
			
			for (int j = 0; j < 3; j++) {
				gvd.tangents[i+j] = t;
				gvd.biTangents[i+j] = bt;
			}
		}
	}
	
	private static void processTangentsIdx(GenericVertexData gvd, int iStart, int iStop){
		Vector2f[] tex = new Vector2f[3];
		Vector3f[] pos = new Vector3f[3];
		
		muA = 0;
		
		for (int i = iStart; i < iStop; i+=3) {
			for (int j = 0; j < 3; j++) {
				int u = gvd.indices[i+j];
				tex[j] = gvd.texture[u];
				pos[j] = gvd.vertices[u];
			}
			Vector3f t = getTangent(new Vector3f(), pos, tex);
			Vector3f bt = getBiTangent(new Vector3f(), pos, tex);
			
			for (int j = 0; j < 3; j++) {
				int u = gvd.indices[i+j];
				if(checktangent(t, gvd.tangents[u]) == 1 || checktangent(bt, gvd.biTangents[u]) == 1){
					Vector3f tt = t;
					t = bt;
					bt = tt;
				}
			}
			
			for (int j = 0; j < 3; j++) {
				int u = gvd.indices[i+j];
				gvd.tangents[u] = mapCompare(t, gvd.tangents[u], u);
				gvd.biTangents[u] = mapCompare(bt, gvd.biTangents[u], -u);
			}
		}
	}
	
	private static int checktangent(Vector3f n, Vector3f p){
		if(p == null)return 0;
		float q = n.distanceSquared(p);
		if(q < 0.2) return 0;
		if(q > 3.99 && q < 4.01)return 1;
		return -1;
	}
	
	private static Vector3f mapCompare(Vector3f n, Vector3f p, int idx){
		if(!main.Settings.debugPrint || p == null || muA >= 10) return n;
		if(checktangent(n, p) == -1){
			debug.Debug.println("* Tangents don't match - Idx:"+idx+" D:"+n.distanceSquared(p), debug.Debug.WARN);
			muA ++;
		}
		return n;
	}
	
	private static Vector3f edge1 = new Vector3f();
	private static Vector3f edge2 = new Vector3f();
	private static Vector2f deltaUV1 = new Vector2f();
	private static Vector2f deltaUV2 = new Vector2f();
	public static synchronized Vector3f getTangent(Vector3f res, Vector3f[] pos, Vector2f[] t){
		edge1.set(pos[1]);
		edge1.sub(pos[0]);
		edge2.set(pos[2]);
		edge2.sub(pos[0]);
		deltaUV1.set(t[1].x-t[0].x, t[1].y-t[0].y);
		deltaUV2.set(t[2].x-t[0].x, t[2].y-t[0].y);
		
		float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

		edge1.mul(deltaUV2.y);
		edge2.mul(deltaUV1.y);
		res.set(edge1);
		res.sub(edge2);
		res.mul(f);
		
		return res.normalize();
	}
	
	public static synchronized Vector3f getBiTangent(Vector3f res, Vector3f[] pos, Vector2f[] t){
		edge1.set(pos[1]);
		edge1.sub(pos[0]);
		edge2.set(pos[2]);
		edge2.sub(pos[0]);
		deltaUV1.set(t[1].x-t[0].x, t[1].y-t[0].y);
		deltaUV2.set(t[2].x-t[0].x, t[2].y-t[0].y);
		
		float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

		edge1.mul(-deltaUV2.x);
		edge2.mul(-deltaUV1.x);
		res.set(edge1);
		res.sub(edge2);
		res.mul(f);
		
		return res.normalize();
	}
}
