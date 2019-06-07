package main.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import debug.PerformanceMonitor;

public class ShaderProgram {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;
	
	private Map<String, Integer> uniforms;

	/**
	 * Creates an empty Shader-Program
	 * @throws Exception
	 */
	public ShaderProgram() throws Exception {
		programId = glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create Shader");
		}
		
		uniforms = new HashMap<>();
	}

	public ShaderProgram(String filepath) throws Exception {
		this();
		createFragmentShader(utility.ResourceLoader.loadResourceString(filepath+".fs"));
		createVertexShader(utility.ResourceLoader.loadResourceString(filepath+".vs"));
		link();
	}

	public void createVertexShader(String shaderCode) throws Exception {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws Exception {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	protected int createShader(String shaderCode, int shaderType) throws Exception {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new Exception("Error creating shader. Type: " + shaderType);
		}

		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void link() throws Exception {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}

		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
		}

	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void dispose() {
		unbind();
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}
	
	private void markStart(){
		debug.PerformanceMonitor.timeGpu.markCPU_done();
	}
	
	private void markStop(){
		debug.PerformanceMonitor.timeGpu.markBUS_done();
	}
	
	/**-------------UNIFORMS--------------------------*/
	
	public int getUniformLocation(String s){
		if(uniforms.containsKey(s)){
			return uniforms.get(s);
		}
		int u = glGetUniformLocation(programId, s);
		uniforms.put(s, u);
		
		if(u < 0){
			debug.Debug.println("*WARNING: Uniform dosn't exist - "+s, debug.Debug.WARN);
		}
		
		return u;
	}
	
	public void setUniformM(String location, Matrix4f m){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		try (MemoryStack s = MemoryStack.stackPush()) {
			FloatBuffer fb = s.mallocFloat(16);
			m.get(fb);
			glUniformMatrix4fv(l, false, fb);
		}
		markStop();
	}
	
	public void setUniformM(String location, Matrix3f m){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		try (MemoryStack s = MemoryStack.stackPush()) {
			FloatBuffer fb = s.mallocFloat(9);
			m.get(fb);
			glUniformMatrix4fv(l, false, fb);
		}
		markStop();
	}
	
	public void setUniformF(String location, float x, float y, float z, float w){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform4f(l, x, y, z, w);
		markStop();
	}
	
	public void setUniformF(String location, Vector4f v){
		setUniformF(location, v.x,  v.y,  v.z, v.w);
	}
	
	public void setUniformF(String location, float x, float y, float z){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform3f(l, x, y, z);
		markStop();
	}
	
	public void setUniformF(String location, Vector3f v){
		setUniformF(location, v.x,  v.y,  v.z);
	}
	
	public void setUniformF(String location, float x, float y){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform2f(l, x, y);
		markStop();
	}
	
	public void setUniformF(String location, Vector2f v){
		setUniformF(location, v.x,  v.y);
	}
	
	public void setUniformF(String location, float f){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform1f(l, f);
		markStop();
	}
	
	public void setUniformI(String location, int i){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform1i(l, i);
		markStop();
	}
	
	public void setUniformI(String location, int i1, int i2){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform2i(l, i1, i2);
		markStop();
	}
	
	public void setUniformI(String location, int i1, int i2, int i3){
		int l = getUniformLocation(location);
		if(l < 0)return;
		
		markStart();
		glUniform3i(l, i1, i2, i3);
		markStop();
	}
}
