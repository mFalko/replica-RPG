package com.falko.android.snowball.utility;

public class Edge {

	private int dest_;
	private int source_;
	private float weight_;
	
	public Edge() {
		this(-1, -1, 0);
	}
	
	public Edge(int source, int dest) {
		this(source, dest, 0);
	}

	public Edge(int source, int dest, float weight) {
		dest_ = dest;
		source_ = source;
		weight_ = weight;
	}

	/**
	 * @param dest
	 *            the dest to set
	 */
	protected void setDest(int dest) {
		dest_ = dest;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	protected void setSource(int source) {
		source_ = source;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(float weight) {
		weight_ = weight;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDest() {
		return dest_;
	}

	/**
	 * 
	 * @return
	 */
	public int getSource() {
		return source_;
	}
	
	/**
	 * @return the weight
	 */
	public float getWeight() {
		return weight_;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Edge) {
			Edge edge = (Edge) other;
			return (dest_ == edge.dest_ && source_ == edge.source_ && weight_ == edge.weight_);
		}

		return false;
	}

	@Override
	public int hashCode() {
		//TODO: Knuth's Multiplicative Method?
		return super.hashCode();
	}

	public String toString() {
		return super.toString();
	}

}
