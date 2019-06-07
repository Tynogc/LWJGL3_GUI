/**
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met: 
 *
 *	* Redistributions of source code must retain the above copyright notice, this
 *	  list of conditions and the following disclaimer. 
 *
 *	* Redistributions in binary
 *	  form must reproduce the above copyright notice, this list of conditions and
 *	  the following disclaimer in the documentation and/or other materials provided
 *	  with the distribution. 
 *
 *	* Neither the name of the Matt DesLauriers nor the names
 *	  of his contributors may be used to endorse or promote products derived from
 *	  this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGE.
 */
package main.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * A very thin wrapper around OpenGL Frame Buffer Objects, intended for
 * 2D purposes. This uses GL_FRAMEBUFFER_EXT for GL 2.1 compatibility.
 * 
 * @author davedes
 */
public class FrameBuffer implements ITexture {
	
	public static boolean isSupported() {
		return main.Main.capabilities.GL_EXT_framebuffer_object;
	}
	
	/** The ID of the FBO in use */
	protected int id;
	protected Texture texture;
	protected boolean ownsTexture;
	protected int depthRenderBufferID;
	
	FrameBuffer(Texture texture, boolean ownsTexture, int depthbuffer) throws Exception {
		this.texture = texture;
		this.ownsTexture = ownsTexture;
		depthRenderBufferID = depthbuffer;
		if (!isSupported()) {
			throw new Exception("FBO extension not supported in hardware");
		}
		GL11.glEnable(GL11.GL_DEPTH_BUFFER_BIT);
		texture.bind();
		id = glGenFramebuffersEXT();
		if(depthRenderBufferID < 0){
			depthRenderBufferID = glGenRenderbuffersEXT();
			glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);                // bind the depth renderbuffer
		    glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, texture.getWidth(), texture.getHeight()); // get the data space for it
		}
		glBindFramebufferEXT(GL_FRAMEBUFFER, id);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0_EXT,
							 	  texture.getTarget(), texture.getID(), 0);
		
		// initialize depth renderbuffer
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer
		
		int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT); 
		switch ( framebuffer ) {
		    case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
		        break;
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
		        throw new RuntimeException( "FrameBuffer: " + id
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
		    default:
		        throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
		}
		glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Advanced constructor which creates a frame buffer from a texture; the framebuffer
	 * does not "own" the texture and thus calling dispose() on this framebuffer will not
	 * destroy the texture. 
	 * 
	 * @param texture the texture to use
	 * @throws Exception 
	 * @throws LWJGLException if the framebuffer was not initialized correctly
	 */
	public FrameBuffer(Texture texture, int depthbuffer) throws Exception {
		this(texture, false, depthbuffer);
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param filter
	 * @param wrap
	 * @throws Exception 
	 */
	public FrameBuffer(int width, int height, int filter, int wrap, int depthbuffer) throws Exception {
		this(new Texture(width, height, filter, wrap), true, depthbuffer);
	}
	
	public FrameBuffer(int width, int height, int filter,  int depthbuffer) throws Exception {
		this(width, height, filter, Texture.DEFAULT_WRAP, depthbuffer);
	}
	
	public FrameBuffer(int width, int height,  int depthbuffer) throws Exception {
		this(width, height, Texture.DEFAULT_FILTER, Texture.DEFAULT_WRAP, depthbuffer);
	}
	
	public FrameBuffer(int width, int height) throws Exception {
		this(width, height, Texture.DEFAULT_FILTER, Texture.DEFAULT_WRAP, -1);
	}
	
	public int getDepthRenderBufferID() {
		return depthRenderBufferID;
	}
	
	public int getID() {
		return id;
	}
		
	public int getWidth() {
		return texture.getWidth();
	}
	
	public int getHeight() {
		return texture.getHeight();
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	/**
	 * Binds the FBO and sets glViewport to the texture region width/height.
	 */
	public void begin() {
		if (id == 0)
			throw new IllegalStateException("can't use FBO as it has been destroyed..");
		glViewport(0, 0, getWidth(), getHeight());
	    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
		//GL11.glReadBuffer(GL_COLOR_ATTACHMENT0);
	}
	
	/**
	 * Unbinds the FBO and resets glViewport to the display size.
	 */
	public void end() {
		if (id==0)
			return;
		
		glViewport(0, 0, main.Main.width, main.Main.height);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	/**
	 * Disposes this FBO without destroying the texture.
	 */
	public void dispose() {
		if (id==0)
			return;
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		glDeleteFramebuffersEXT(id);
		glDeleteRenderbuffers(depthRenderBufferID);
		if (ownsTexture)
			texture.dispose();
		id = 0;
		//glReadBuffer(GL_BACK);
	}

	@Override
	public float getU() {
		return 0;
	}

	@Override
	public float getV() {
		return 1f;
	}

	@Override
	public float getU2() {
		return 1f;
	}

	@Override
	public float getV2() {
		return 0;
	}
}