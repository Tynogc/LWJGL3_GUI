package main.graphics.obj;

public interface Mesh {

	public abstract int getVaoId();

	public abstract int getVertexCount();

	public abstract void render();

	public abstract void cleanUp();

}