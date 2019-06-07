package main.graphics.obj;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import main.CleanUpProcessor;

import org.lwjgl.system.MemoryUtil;

public class Mesh_Nom implements Mesh{

	private final int vaoId;

	private int vboIds[];

	private final int vertexCount;
	
	private CleanUpProcessor.CleanUpAction cc;

	public Mesh_Nom(AttributeData[] atd) {
		vboIds = new int[atd.length];
		
		FloatBuffer[] buffers = new FloatBuffer[atd.length];
		try {
			vertexCount = atd[0].getDataSize()/atd[0].size;

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			int u = 0;
			for (AttributeData ad : atd) {
				vboIds[u] = glGenBuffers();
				buffers[u] = MemoryUtil.memAllocFloat(ad.getDataSize());
				ad.fill(buffers[u]);
				buffers[u].flip();
				glBindBuffer(GL_ARRAY_BUFFER, vboIds[u]);
				glBufferData(GL_ARRAY_BUFFER, buffers[u], GL_STATIC_DRAW);
				glVertexAttribPointer(ad.index, ad.size, GL_FLOAT, false, 0, 0);
				
				u++;
			}

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			for (FloatBuffer f : buffers) {
				if(f != null)
					MemoryUtil.memFree(f);
			}
		}
		
		CleanUpProcessor.addCleanJob(cc = new CleanUpProcessor.CleanUpAction() {
			@Override
			public void clean() {
				cleanUp();
			}
		});
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void render() {
		debug.PerformanceMonitor.timeGpu.markCPU_done();
		// Draw the mesh
		glBindVertexArray(getVaoId());
		for (int i = 0; i < vboIds.length; i++) {
			glEnableVertexAttribArray(i);
		}

		debug.PerformanceMonitor.timeGpu.markBUS_done();
		glDrawArrays(GL_TRIANGLES, 0, getVertexCount());
		debug.PerformanceMonitor.timeGpu.markGPU_done();

		// Restore state
		for (int i = 0; i < vboIds.length; i++) {
			glDisableVertexAttribArray(i);
		}
		glBindVertexArray(0);
		
		debug.PerformanceMonitor.timeGpu.markBUS_done();
	}

	public void cleanUp() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int i = 0; i < vboIds.length; i++) {
			glDeleteBuffers(vboIds[i]);
		}

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		CleanUpProcessor.removeCleanJob(cc);
	}
	
}
