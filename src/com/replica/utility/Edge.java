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
