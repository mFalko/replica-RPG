/**
 * 
 */
package com.replica.utility;

import java.io.InputStream;


/**
 * @author matt
 *
 */
public abstract class AbstractGraph implements Graph {
	
	public AbstractGraph(int vertexCount, boolean isDirected) {
		this(vertexCount, isDirected, DEFAULT_EDGE_POOL_SIZE);
	}

	public AbstractGraph(int vertexCount, boolean isDirected, int size) {
		EDGE_POOL_SIZE = size;
		edgePool_ = new EdgePool(EDGE_POOL_SIZE);
		isDirected_ = isDirected;
		vertexCount_ = vertexCount;
		
	}

	public int getVertexCount() {
		return vertexCount_;
	}

	public boolean isDirected() {
		return isDirected_;
	}


	public abstract void LoadFromFile(InputStream in);
	
	public static Graph createGraph(InputStream in, boolean isDirected, GraphType type) {
		return null;
	}
	
	protected Edge newEdge(int source, int dest) {
		Edge newEdge = edgePool_.allocate();
		newEdge.setDest(dest);
		newEdge.setSource(source);
		return newEdge;
	}
	
	protected Edge newEdge() {
		return edgePool_.allocate();
	}
	
	protected void  freeEdge(Edge edge) {
		edgePool_.release(edge);
	}
	
	private class EdgePool extends TObjectPool<Edge> {

		public EdgePool(int size) {
			super(size);
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
                getAvailable().add(new Edge());
            }
		}
		
	}
	
	private boolean isDirected_;
	private int vertexCount_;
	private EdgePool edgePool_;
	
	private final int EDGE_POOL_SIZE;
	private static final int DEFAULT_EDGE_POOL_SIZE = 32;
}
