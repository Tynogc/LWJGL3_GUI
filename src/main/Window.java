package main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private final String title;

	private int width;

	private int height;

	private long windowHandle;

	private boolean resized;

	private boolean vSync;

	private static List<ResizeListener> listeners;
	
	private GLFWKeyCallbackI keyCallback;
	private GLFWMouseButtonCallbackI mouseButtonCallback;
	private GLFWCursorPosCallbackI cursorCallback;
	private GLFWCharCallbackI charCallback;
	
	private InputHandler iph;

	public Window(String title, int width, int height, boolean vSync) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.vSync = vSync;
		this.resized = true;

		listeners = new ArrayList<>();
		
		iph = new InputHandler();
	}

	public void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		// Create the window
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup resize callback
		glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.setResized(true);
		});
		
		glfwSetKeyCallback(windowHandle, keyCallback = (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
			iph.keyAction(key, scancode, action, mods);
		});
		
		glfwSetCursorPosCallback(windowHandle, cursorCallback = (window, xpos, ypos) -> {
			iph.mouseMovement(xpos, ypos);
		});
		
		glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback = (window, key, action, mods) -> {
			iph.mouseAction(key, action, mods);
		});
		
		glfwSetCharCallback(windowHandle, charCallback = (window, type) -> {
			iph.keyAction((char)type);
		});
		
		//glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED); TODO?
		
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2,
				(vidmode.height() - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);

		setvSync(vSync);

		// Make the window visible
		glfwShowWindow(windowHandle);

		main.Main.capabilities = GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		// glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

		// Support for transparencies
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		iph.setController(GLFW_JOYSTICK_1);
	}

	public long getWindowHandle() {
		return windowHandle;
	}

	public void setClearColor(float r, float g, float b, float alpha) {
		glClearColor(r, g, b, alpha);
	}

	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}

	public boolean windowShouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isResized() {
		return resized;
	}

	public void setResized(boolean resized) {
		this.resized = resized;
	}

	public boolean isvSync() {
		return vSync;
	}

	public void setvSync(boolean vSync) {
		this.vSync = vSync;
		if (vSync) {
			glfwSwapInterval(1);
		} else {
			glfwSwapInterval(0);
		}
		debug.LogSaver.isVSync = vSync;
	}

	public void update() {
		glfwSwapBuffers(windowHandle);
		glfwPollEvents();
	}

	public void close() {
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
	}

	public void knockResize() {
		if (resized) {
			for (ResizeListener r : listeners) {
				if (r != null)
					r.resize(width, height);
			}
		}
	}

	public static void addResizeListener(ResizeListener r) {
		listeners.add(r);
		r.resize(Main.width, Main.height);
	}

	public static void removeResizeListener(ResizeListener r) {
		listeners.remove(r);
	}

	public interface ResizeListener {
		public void resize(int w, int h);
	}
	
	public InputHandler getIph() {
		return iph;
	}
}
