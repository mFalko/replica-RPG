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

import java.util.Iterator;

/**
 * @author matt
 *
 */
public interface Graph {

	public abstract int getVertexCount();
	
	public abstract boolean isDirected();
	
	public abstract void insert(Edge edge);
	
	public abstract boolean isEdge(int source, int dest);
	
	public abstract Edge getEdge(int source, int dest);
	
	public abstract Iterator<Edge> edgeIterator(int source);
	
	enum GraphType {
		MATRIX,
		LIST
	}
}
