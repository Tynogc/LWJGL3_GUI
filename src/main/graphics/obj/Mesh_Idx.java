package main.graphics.obj;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import main.CleanUpProcessor;

import org.lwjgl.system.MemoryUtil;

public class Mesh_Idx implements Mesh {

	private final int vaoId;

	private int vboIds[];

	private final int vertexCount;

	private final int idxVboId;
	
	private CleanUpProcessor.CleanUpAction cc;

	public Mesh_Idx(AttributeData[] atd, int[] indices) {
		IntBuffer indicesBuffer = null;
		vboIds = new int[atd.length];
		
		FloatBuffer[] buffers = new FloatBuffer[atd.length];
		try {
			vertexCount = indices.length;

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

			// Index VBO
			idxVboId = glGenBuffers();
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			for (FloatBuffer f : buffers) {
				if(f != null)
					MemoryUtil.memFree(f);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
		
		CleanUpProcessor.addCleanJob(cc = new CleanUpProcessor.CleanUpAction() {
			@Override
			public void clean() {
				cleanUp();
			}
		});
	}

	/* (non-Javadoc)
	 * @see main.graphics.obj.Mesh#getVaoId()
	 */
	@Override
	public int getVaoId() {
		return vaoId;
	}

	/* (non-Javadoc)
	 * @see main.graphics.obj.Mesh#getVertexCount()
	 */
	@Override
	public int getVertexCount() {
		return vertexCount;
	}

	/* (non-Javadoc)
	 * @see main.graphics.obj.Mesh#render()
	 */
	@Override
	public void render() {
		debug.PerformanceMonitor.timeGpu.markCPU_done();
		// Draw the mesh
		glBindVertexArray(getVaoId());
		for (int i = 0; i < vboIds.length; i++) {
			glEnableVertexAttribArray(i);
		}

		debug.PerformanceMonitor.timeGpu.markBUS_done();
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		debug.PerformanceMonitor.timeGpu.markGPU_done();

		// Restore state
		for (int i = 0; i < vboIds.length; i++) {
			glDisableVertexAttribArray(i);
		}
		glBindVertexArray(0);
		
		debug.PerformanceMonitor.timeGpu.markBUS_done();
	}

	/* (non-Javadoc)
	 * @see main.graphics.obj.Mesh#cleanUp()
	 */
	@Override
	public void cleanUp() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int i = 0; i < vboIds.length; i++) {
			glDeleteBuffers(vboIds[i]);
		}
		glDeleteBuffers(idxVboId);

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		CleanUpProcessor.removeCleanJob(cc);
	}
}
