package main.graphics.obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class FileImport {
	
	private static Vector2f[] defTex = new Vector2f[]{
			new Vector2f(0), new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1)
	};

	public static GenericVertexData[] importFile(String fp, boolean indexMode){
		return importFile(fp, indexMode, defTex);
	}
	
	public static GenericVertexData[] importFile(String fp, boolean indexMode, Vector2f[] texture){
		BufferedReader br = null;
		try {
			if(main.Settings.debugPrint)
				debug.Debug.println("* Loading m3d "+fp);
			FileReader fr = new FileReader(fp);
			br = new BufferedReader(fr);
			
			return new FileImport(indexMode, texture).load(br);
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading 3D-mesh: "+fp, debug.Debug.ERROR);
			debug.Debug.printException(e);
			return null;
		}finally {
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {}
		}
	}
	
	private List<Vector3f> vertices;
	private List<Vector3f> normals;
	private List<Vector2f> texture;
	
	private List<Group> groups;
	private Group currentGroup;
	
	private int currentSmooth;
	
	private Vector2f[] defTexTu;
	private boolean indexMode;
	
	private int faceCount;
	
	public FileImport(boolean idx, Vector2f[] dt){
		vertices = new ArrayList<>();
		normals = new ArrayList<>();
		texture = new ArrayList<>();
		
		groups = new ArrayList<>();
		defTexTu = dt;
		indexMode = idx;
	}
	
	public GenericVertexData[] load(BufferedReader b) throws IOException{
		String s;
		while((s = b.readLine()) != null)
			stringLoad(s);
		
		GenericVertexData[] gvda = new GenericVertexData[groups.size()];
		int i = 0;
		for (Group g : groups) {
			processNormals(g);
			if(indexMode)
				gvda[i++] = fillIndices(g);
			else
				gvda[i++] = fillSequence(g);
		}
		
		for (i = 0; i < gvda.length; i++) {
			gvda[i].processTangents();
			gvda[i].processSize();
		}
		
		if(main.Settings.debugPrint && indexMode)
			debug.Debug.println("Done! "+faceCount+" Faces in "+groups.size()+" Group(s)");
		if(main.Settings.debugPrint && !indexMode)
			debug.Debug.println("Done! "+faceCount+" Triangles in "+groups.size()+" Group(s)");
		
		return gvda;
	}
	
	private void stringLoad(String s){
		s = s.trim();
		if(s.startsWith("#"))return;//Comments
		if(s.startsWith("o")){
			//name = s.substring(2);
			return;
		}
		if(s.startsWith("g")){
			currentGroup = getGroup(s.substring(2));
			return;
		}
		if(s.startsWith("s")){
			if(s.contains("off"))currentSmooth = 0;
			else currentSmooth = Integer.parseInt(s.substring(1).trim());
			return;
		}
		if(s.startsWith("idm") || s.startsWith("Idm") || s.startsWith("IDM")){ //Toggle Index-Mode
			indexMode = s.contains("ON") || s.contains("on") || s.contains("On");
			return;
		}
		
		String[] st = s.split(" ");
		
		if(s.startsWith("v ")){
			vertices.add(new Vector3f(Float.parseFloat(st[1]), Float.parseFloat(st[2]), Float.parseFloat(st[3])));
		}
		if(s.startsWith("vn")){
			normals.add(new Vector3f(Float.parseFloat(st[1]), Float.parseFloat(st[2]), Float.parseFloat(st[3])));
		}
		if(s.startsWith("vt")){
			texture.add(new Vector2f(Float.parseFloat(st[1]), 1f-Float.parseFloat(st[2])));
		}
		if(st[0].startsWith("f")){
			if(currentGroup == null) currentGroup = getGroup("default");
			
			Face f = new Face(st.length-1);
			for (int i = 1; i < st.length; i++) {
				fillFace(f, st[i], i-1);
			}
			
			f.smoothId = currentSmooth;
			currentGroup.faces.add(f);
			faceCount++;
		}
	}
	
	private void fillFace(Face f, String s, int i){
		String[] st = s.split("/");
		
		f.edgeIdx[i] = Integer.parseInt(st[0]);
		if(st.length==1)return;
		if(st[1].length()>=1){
			f.texIdx[i] = Integer.parseInt(st[1]);
		}else{
			f.texIdx[i] = -1;
		}
		if(st.length==2)return;
		if(st[2].length()>=1){
			f.normIdx[i] = Integer.parseInt(st[2]);
		}
	}
	
	private GenericVertexData fillIndices(Group g){
		List<Vector3f> v = new ArrayList<>();
		List<Vector3f> n = new ArrayList<>();
		List<Vector2f> t = new ArrayList<>();
		List<Integer> idx = new ArrayList<>();
		
		int idxCount = 0;
		GenericVertexData gvd = new GenericVertexData(true);
		for (Face f : g.faces) {
			for (int i = 0; i < f.edgeIdx.length; i++) {
				addMtrb(f,v,n,t,i);
				
				f.idxMap[i] = idxCount++;
			}
			
			for (int i = 2; i < f.idxMap.length; i++) {
				idx.add(f.idxMap[i-1]);
				idx.add(f.idxMap[i]);
				idx.add(f.idxMap[0]);
			}
		}
		gvd.vertices = (Vector3f[]) v.toArray(new Vector3f[v.size()]);
		gvd.normals = (Vector3f[]) n.toArray(new Vector3f[n.size()]);
		gvd.texture = (Vector2f[]) t.toArray(new Vector2f[t.size()]);
		gvd.indices = new int[idx.size()];
		for (int i = 0; i < idx.size(); i++) {
			gvd.indices[i] = idx.get(i);
		}
		
		return gvd;
	}
	
	private GenericVertexData fillSequence(Group g){
		List<Vector3f> v = new ArrayList<>();
		List<Vector3f> n = new ArrayList<>();
		List<Vector2f> t = new ArrayList<>();
		
		GenericVertexData gvd = new GenericVertexData(false);
		for (Face f : g.faces) {
			for (int i = 2; i < f.idxMap.length; i++) {
				if(i>2)faceCount++;
				
				addMtrb(f,v,n,t,i-1);
				addMtrb(f,v,n,t,i);
				addMtrb(f,v,n,t,0);
			}
		}
		gvd.vertices = (Vector3f[]) v.toArray(new Vector3f[v.size()]);
		gvd.normals = (Vector3f[]) n.toArray(new Vector3f[n.size()]);
		gvd.texture = (Vector2f[]) t.toArray(new Vector2f[t.size()]);
		
		return gvd;
	}
	
	private void addMtrb(Face f, List<Vector3f> v, List<Vector3f> n, List<Vector2f> t, int i){
		v.add(new Vector3f(vertices.get(f.edgeIdx[i]-1)));
		if(f.texIdx[i] == -1)t.add(new Vector2f(defTexTu[i%defTexTu.length]));
		else t.add(new Vector2f(texture.get(f.texIdx[i]-1)));
		if(f.processedNormals[i] == null)
			n.add(new Vector3f(normals.get(f.normIdx[i]-1)));
		else
			n.add(f.processedNormals[i]);
	}
	
	private void processNormals(Group g){
		for (Face f1 : g.faces) {
			if(f1.smoothId == 0) continue;
			for (Face f2 : g.faces) {
				if(f1.smoothId != f2.smoothId)continue;
				
				for (int i = 0; i < f1.edgeIdx.length; i++) {
					for (int j = 0; j < f2.edgeIdx.length; j++) {
						if(f1.edgeIdx[i] != f2.edgeIdx[j]) continue;
						
						if(f1.processedNormals[i] == null)
							f1.processedNormals[i] = new Vector3f(normals.get(f1.normIdx[i]-1));
						
						f1.processedNormals[i].add(normals.get(f2.normIdx[j]-1));
					}
				}
			}
			
			for (int i = 0; i < f1.processedNormals.length; i++) {
				if(f1.processedNormals[i] != null)
					f1.processedNormals[i].normalize();
			}
		}
	}
	
	private Group getGroup(String name){
		for (Group g : groups) {
			if(g.name.equals(name))
				return g;
		}
		Group g = new Group(name);
		groups.add(g);
		return g;
	}
	
	private class Group{
		public List<Face> faces;
		
		public final String name;
		
		public Group(String n){
			name = n;
			faces = new ArrayList<>();
		}
	}
	
	private class Face{
		public int[] edgeIdx;
		public int[] normIdx;
		public int[] texIdx;
		
		//Resulting indices for rendering
		public int[] idxMap;
		
		public Vector3f[] processedNormals;
		
		public int smoothId;
		
		private Face(int size){
			edgeIdx = new int[size];
			normIdx = new int[size];
			texIdx = new int[size];
			
			idxMap = new int[size];
			processedNormals = new Vector3f[size];
		}
	}
	
}
