package area;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import utility.GeometryConstants;
import main.graphics.Camera;
import main.graphics.Projection;
import main.graphics.ShaderProgram;
import main.graphics.obj.AttributeData;
import main.graphics.obj.Mesh_Idx;
import main.graphics.obj.Mesh_Nom;

public class LandscapeShader {

	public static final String U_MAT_TRANSFORMATION = "u_transformation1";
	public static final String U_MAT_TRANSFORMATION2 = "u_transformation2";
	public static final String U_MAT_PROJECTION = "u_projection";
	public static final String U_CAMERA_POS = "u_cameraPos";
	
	public ShaderProgram shader;
	
	public static LandscapeShader lss;
	
	public LandscapeShader() throws Exception{
		lss = this;
		shader = new ShaderProgram("res/sha/landscape");
	}
	
	public void prepare(Camera c, Projection p, Matrix4f mat){
		shader.bind();
		shader.setUniformM(U_MAT_PROJECTION, p.getMatrix(c, mat));
		shader.setUniformM(U_MAT_TRANSFORMATION, GeometryConstants.identity);
		shader.setUniformM(U_MAT_TRANSFORMATION2, GeometryConstants.identity);
		shader.setUniformF(U_CAMERA_POS, c.pos);
		shader.setUniformI("tex1", 0);
		shader.setUniformI("tex2", 1);
	}
	
	public void unbind(){
		shader.unbind();
	}
	
	public void setTransformation(Matrix4f m){
		shader.setUniformM(U_MAT_TRANSFORMATION, m);
	}
	
	public static Mesh_Nom generateMeshSeq(Vector3f[] v, Vector3f[] n, Vector2f[] t, Vector3f[] ta, Vector3f[] bt){
		return new Mesh_Nom(new AttributeData[]{
				new AttributeData(0, 3, v),
				new AttributeData(1, 3, n),
				new AttributeData(2, 2, t),
				new AttributeData(3, 3, ta),
				new AttributeData(4, 3, bt),
			});
	}
	
	public static Mesh_Idx generateMeshIdx(Vector3f[] v, Vector3f[] n, Vector2f[] t, Vector3f[] ta, Vector3f[] bt, int[] idx){
		return new Mesh_Idx(new AttributeData[]{
				new AttributeData(0, 3, v),
				new AttributeData(1, 3, n),
				new AttributeData(2, 2, t),
				new AttributeData(3, 3, ta),
				new AttributeData(4, 3, bt),
			}, idx);
	}
}
