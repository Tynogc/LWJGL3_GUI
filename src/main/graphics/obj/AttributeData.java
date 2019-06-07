package main.graphics.obj;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class AttributeData {

	public final int index;
	
	public final int size;
	
	public DataInterface data;
	
	public AttributeData(int i, int s){
		index = i;
		size = s;
	}
	
	public AttributeData(int i, int s, float[] d){
		this(i, s);
		setData(d);
	}
	
	public void setData(float[] d){
		data = new DataFloat(d);
	}
	
	public AttributeData(int i, int s, Vector2f[] d){
		this(i, s);
		setData(d);
	}
	
	public void setData(Vector2f[] d){
		data = new DataVec2(d);
	}
	
	public AttributeData(int i, int s, Vector3f[] d){
		this(i, s);
		setData(d);
	}
	
	public void setData(Vector3f[] d){
		data = new DataVec3(d);
	}
	
	public AttributeData(int i, int s, Vector4f[] d){
		this(i, s);
		setData(d);
	}
	
	public void setData(Vector4f[] d){
		data = new DataVec4(d);
	}
	
	public int getDataSize(){
		if(data == null) return 0;
		return data.getSize();
	}
	
	public void fill(FloatBuffer f){
		if(data != null)
			data.fill(f);
	}
	
	private interface DataInterface{
		public void fill(FloatBuffer f);
		public int getSize();
	}
	
	private class DataFloat implements DataInterface{
		private final float[] m;
		
		 public DataFloat(float[] m) {
			this.m = m;
		}

		@Override
		public void fill(FloatBuffer f) {
			f.put(m);
		}

		@Override
		public int getSize() {
			return m.length;
		}
	}
	
	private class DataVec2 implements DataInterface{
		private final Vector2f[] m;
		
		 public DataVec2(Vector2f[] m) {
			this.m = m;
		}

		@Override
		public void fill(FloatBuffer f) {
			for (int i = 0; i < m.length; i++) {
				f.put(m[i].x).put(m[i].y);
			}
		}

		@Override
		public int getSize() {
			return m.length*2;
		}
	}
	
	private class DataVec3 implements DataInterface{
		private final Vector3f[] m;
		
		 public DataVec3(Vector3f[] m) {
			this.m = m;
		}

		@Override
		public void fill(FloatBuffer f) {
			for (int i = 0; i < m.length; i++) {
				f.put(m[i].x).put(m[i].y).put(m[i].z);
			}
		}

		@Override
		public int getSize() {
			return m.length*3;
		}
	}
	
	private class DataVec4 implements DataInterface{
		private final Vector4f[] m;
		
		 public DataVec4(Vector4f[] m) {
			this.m = m;
		}

		@Override
		public void fill(FloatBuffer f) {
			for (int i = 0; i < m.length; i++) {
				f.put(m[i].x).put(m[i].y).put(m[i].z).put(m[i].w);
			}
		}

		@Override
		public int getSize() {
			return m.length*4;
		}
	}
	
}
