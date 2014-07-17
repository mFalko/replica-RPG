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
import java.util.Iterator;

/**
 * @author matt
 *
 */
public class MatrixGraph extends AbstractGraph {

	int[][] adjMatrix_;
	
	/**
	 * 
	 */
	public MatrixGraph(int vertexCount, boolean isDirected) {
		super(vertexCount, isDirected);
		adjMatrix_ = new int[vertexCount][vertexCount];
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#insert(com.falko.android.snowball.utility.Edge)
	 */
	public void insert(Edge edge) {
		
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#isEdge(int, int)
	 */
	public boolean isEdge(int source, int dest) {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#getEdge(int, int)
	 */
	public Edge getEdge(int source, int dest) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#edgeIterator(int)
	 */
	public Iterator<Edge> edgeIterator(int source) {
		
		return null;
	}

	public LList<Edge> getAdjacent(int vertex) {
		
		return null;
	}

	@Override
	public void LoadFromFile(InputStream in) {
		
		
	}


}
