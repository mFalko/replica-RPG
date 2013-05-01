/**
 * 
 */
package com.replica.utility;

import java.io.InputStream;
import java.util.Iterator;

/**
 * @author matt
 *
 */
public class ListGraph extends AbstractGraph {

	private FixedSizeArray<LList<Edge>> edges_;
	
	
	/**
	 * 
	 */
	public ListGraph(int vertexCount, boolean isDirected) {
		super(vertexCount, isDirected);
		edges_ = new FixedSizeArray<LList<Edge>>(vertexCount);
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#insert(com.falko.android.snowball.utility.Edge)
	 */
	public void insert(Edge edge) {
		int from = edge.getSource();
		int to = edge.getDest();
		
		edges_.get(from).add(edge);
		if (!isDirected()) {
			edges_.get(to).add(newEdge(to, from));
		}
	}

	/* (non-Javadoc)
	 * @see com.falko.android.snowball.utility.Graph#isEdge(int, int)
	 */
	public boolean isEdge(int source, int dest) {
		Edge edge = newEdge(source, dest);
		boolean isEdge = edges_.get(source).contains(edge);
		freeEdge(edge);
		return isEdge;
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
		return edges_.get(vertex);
	}

	@Override
	public void LoadFromFile(InputStream in) {
		
	}
}
