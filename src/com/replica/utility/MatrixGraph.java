/**
 * 
 */
package com.falko.android.snowball.utility;

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
