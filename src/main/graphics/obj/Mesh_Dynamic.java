package main.graphics.obj;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import main.CleanUpProcessor;

import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

public class Mesh_Dynamic implements Mesh{

	public final int vaoId;
	
	public final RefreshableAtributeArray[] content;
	
	public int currentVertexCount;
	
	public final int size;
	
	private CleanUpProcessor.CleanUpAction cc;
	
	public Mesh_Dynamic(AttributeData[] attributes, int size) {
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		this.size = size;
		
		content = new RefreshableAtributeArray[attributes.length];
		int u = 0;
		for (AttributeData at : attributes) {
			RefreshableAtributeArray ra = new RefreshableAtributeArray();
			ra.vbo = glGenBuffers();
			ra.buf = MemoryUtil.memAllocFloat(size*at.size);
			glBindBuffer(GL_ARRAY_BUFFER, ra.vbo);
			glVertexAttribPointer(at.index, at.size, GL_FLOAT, false, 0, 0);
			
			content[u++] = ra;
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		CleanUpProcessor.addCleanJob(cc = new CleanUpProcessor.CleanUpAction() {
			@Override
			public void clean() {
				cleanUp();
			}
		});
	}
	
	@Override
	public int getVaoId() {
		return vaoId;
	}

	@Override
	public int getVertexCount() {
		return currentVertexCount;
	}
	
	public FloatBuffer getBuffer(int i){
		return content[i].buf;
	}

	@Override
	public void render() {
		debug.PerformanceMonitor.timeGpu.markCPU_done();
		for (RefreshableAtributeArray ra : content) {
			ra.buf.rewind();
			glBindBuffer(GL_ARRAY_BUFFER, ra.vbo);
			glBufferData(GL_ARRAY_BUFFER, ra.buf, GL15.GL_STREAM_DRAW);
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		// Draw the mesh
		glBindVertexArray(vaoId);
		for (int i = 0; i < content.length; i++)
			glEnableVertexAttribArray(i);

		debug.PerformanceMonitor.timeGpu.markBUS_done();
		glDrawArrays(GL_TRIANGLES, 0, currentVertexCount);
		debug.PerformanceMonitor.timeGpu.markGPU_done();

		// Restore state
		for (int i = 0; i < content.length; i++)
			glDisableVertexAttribArray(i);
		glBindVertexArray(0);
		debug.PerformanceMonitor.timeGpu.markBUS_done();
		
		currentVertexCount = 0;
	}

	@Override
	public void cleanUp() {
		glDisableVertexAttribArray(0);
		
		for (RefreshableAtributeArray ra : content) {
			glDeleteBuffers(ra.vbo);
		}
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		for (RefreshableAtributeArray ra : content) {
			MemoryUtil.memFree(ra.buf);
		}
		
		CleanUpProcessor.removeCleanJob(cc);
	}
	
	private class RefreshableAtributeArray{
		private FloatBuffer buf;
		private int vbo;
	}
}
