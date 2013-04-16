/**
 * 
 */
package com.falko.android.snowball.utility;

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
