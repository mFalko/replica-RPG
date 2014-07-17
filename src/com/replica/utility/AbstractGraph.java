/*
 * Copyright (C) 2010 Matthew Falkoski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
