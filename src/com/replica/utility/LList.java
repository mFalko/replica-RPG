/*
 * LList.java
 *
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

import com.replica.core.AllocationGuard;

/**
 * LList<T> </br> This Class contains the definition of a circular doubly linked
 * list </br>
 * 
 * @author Matt Falkoski
 * @version 1.1
 * 
 *          Compiler: Java 1.6 <br>
 *          Hardware: PC<br>
 * 
 *          May 4, 2010 <br>
 *          MF completed v 1.0
 * 
 *          Sep 20, 2012 <br>
 *          MF add in node pool
 */

public class LList<T> implements Iterable<T> {
	/*
	 * ====================================================================== ==
	 * Public Interface
	 * ======================================================================
	 */
	/**
	 * Default constructor </br> Creates a new list with capacity 32
	 */
	public LList() {
		this(DEFAULT_MAX_SIZE);
	}

	/**
	 * Constructor </br> Creates a new list with capacity size
	 * 
	 * @param size
	 *            the max capacity of the list
	 */
	public LList(int size) {
		nodePool_ = new LLNodePool(size);
		headNode_ = nodePool_.allocate();
		length_ = 0;
		iterator_ = new LLIterator();
		reverseIterator_ = new Reverse_LLIterator();
	}

	/**
	 * Constructor </br> Creates a new list with capacity size
	 * 
	 * @param size
	 *            the max capacity of the list
	 */
	public LList(LLNodePool pool) {
		nodePool_ = pool;
		headNode_ = nodePool_.allocate();
		length_ = 0;
		iterator_ = new LLIterator();
		reverseIterator_ = new Reverse_LLIterator();
	}

	public final void clear() {
		if (isEmpty())
			return;

		while (length_ > 0) {
			remove(0);
		}
		
		iterator_.reset();
		reverseIterator_.reset();
	}

	public int getLength() {
		return length_;
	}

	public LLNodePool getPool() {
		return nodePool_;
	}
	
	public T pop() {
		return remove(0);
	}
	
	public boolean push(T item) {
		return add(0, item);
	}

	public boolean add(T newEntry) {
		if (!nodePool_.canAllocate()) {
			return false;
		}
		
		LLNode newNode = nodePool_.allocate(newEntry);
		
		if (isEmpty()) {
			attach(headNode_, newNode);
			attach(newNode, headNode_);
		} else {
			attach(headNode_.previous(), newNode);
			attach(newNode, headNode_);
		}
		++length_;
		return true;
	}

	public boolean add(int newPosition, T newEntry) {
		if (isEmpty() || newPosition == length_)
			return add(newEntry);
		if (newPosition >= 0 && newPosition < length_ && nodePool_.canAllocate()) {
			LLNode newNode = nodePool_.allocate(newEntry);
			if (newPosition == 0) {
				attach(newNode, headNode_.next());
				attach(headNode_, newNode);
			} else {
				LLNode nodeBefore = getNodeAt(newPosition - 1);
				LLNode nodeAfter = nodeBefore.next();
				attach(nodeBefore, newNode);
				attach(newNode, nodeAfter);
			}
			length_++;
			return true;
		} else
			return false;
	}

	public void display() {
		Iterator<T> itr = begin();
		while (itr.hasNext())
			DebugLog.v("LList", itr.next().toString());
	}

	public void rDisplay() {
		Iterator<T> itr = rBegin();
		while (itr.hasNext())
			DebugLog.v("LList", itr.next().toString());
	}

	public boolean isEmpty() {
		if (length_ == 0) {
			assert headNode_.next() == null && headNode_.previous() == null;
			return true;
		} else {
			assert headNode_.next() != null && headNode_.previous() != null;
			return false;
		}
	}

	public boolean replace(int givenPosition, T newEntry) {
		if (givenPosition >= 0 && givenPosition < length_ && !isEmpty()) {
			LLNode desiredNode = getNodeAt(givenPosition);
			desiredNode.setData(newEntry);
			return true;
		}
		return false;
	}

	public T getEntry(int givenPosition) {
		T result = null;
		if ((givenPosition >= 0) && (givenPosition < length_)) {
			assert !isEmpty();
			result = getNodeAt(givenPosition).data();
		}
		return result;
	}

	public T remove(int givenPosition) {
		T result = null;
		if ((givenPosition >= 0) && (givenPosition < length_)) {
			assert !isEmpty();
			LLNode nodeToRemove = getNodeAt(givenPosition);
			result = nodeToRemove.data();
			attach(nodeToRemove.previous(), nodeToRemove.next());
			nodePool_.release(nodeToRemove);
			length_--;
		}
		return result; // return removed entry, or
						// null if operation fails
	}

	public boolean remove(T toRemove) {
		Iterator<T> itr = begin();
		while (itr.hasNext()) {
			if (itr.next().equals(toRemove)) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	public boolean isFull() {
		return nodePool_.canAllocate();
	}

	public boolean contains(T anEntry) {
		if (isEmpty())
			return false;
		LLNode currentNode = headNode_.next();
		while (currentNode != headNode_) {
			if (anEntry.equals(currentNode.data()))
				return true;
			else
				currentNode = currentNode.next();
		}
		return false;
	}

	/**
	 * @return an Iterator over the list
	 */
	public Iterator<T> begin() {
		iterator_.reset();
		return iterator_;
	}

	/**
	 * @return a Reverse Iterator over the list
	 */
	public Iterator<T> rBegin() {
		reverseIterator_.reset();
		return reverseIterator_;
	}

	/**
	 * @return an Iterator over the list
	 */
	public Iterator<T> iterator() {
		return begin();
	}

	/**
	 * clears the list before the GC collects the memory
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
		nodePool_.release(headNode_);
	}

	/*
	 * ====================================================================== ==
	 * Private methods
	 * ======================================================================
	 */
	/**
	 * returns the node at the given position
	 * 
	 * @param givenPosition
	 *            the position on the list
	 */
	private LLNode getNodeAt(int givenPosition) {
		assert !isEmpty() && (givenPosition < 0 || givenPosition >= length_);

		LLNode currentNode = headNode_.next();
		for (int counter = 0; counter < givenPosition; counter++)
			currentNode = currentNode.next();
		assert currentNode != headNode_;
		return currentNode;
	}

	/**
	 * attaches two nodes together
	 * 
	 * @param first
	 *            the item before second
	 * @param second
	 *            the item after first
	 */
	private void attach(LLNode first, LLNode second) {
		first.setNext(second);
		second.setPrevious(first);
	}

	/**
	 * A Node class for the list
	 * 
	 * @author Matt Falkoski
	 */
	private class LLNode extends AllocationGuard {

		private T data_;
		private LLNode next_;
		private LLNode previous_;

		/**
		 * 
		 */
		private LLNode() {
			super();
			reset();
		}

		/**
		 * 
		 * @param data
		 */
		private LLNode(T data) {
			this();
			data_ = data;
		}

		private void reset() {
			data_ = null;
			next_ = null;
			previous_ = null;
		}

		private LLNode next() {
			return next_;
		}

		private LLNode previous() {
			return previous_;
		}

		private T data() {
			return data_;
		}

		private void setNext(LLNode n) {
			next_ = n;
		}

		private void setPrevious(LLNode p) {
			previous_ = p;
		}

		private void setData(T d) {
			data_ = d;
		}
	}

	/**
	 * 
	 * @author matt
	 * 
	 */
	public class LLNodePool extends TObjectPool<LLNode> {

		private LLNodePool(int size) {
			super(size);
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				getAvailable().add(new LLNode());
			}
		}

		public boolean canAllocate() {
			return getSize() - getAllocatedCount() != 0;
		}

		public LLNode allocate(T data) {
			LLNode newNode = super.allocate();
			newNode.setData(data);
			return newNode;
		}

		public void release(Object entry) {
			@SuppressWarnings("unchecked")
			LLNode node = (LLNode) entry;
			node.reset();
			super.release(node);
		}

	}

	/**
	 * An Iterator to traverse the list
	 * 
	 * @author Matt Falkoski
	 */
	public class LLIterator  extends AllocationGuard implements Iterator<T> {
		private LLNode current = null;

		/**
		 * Constructor
		 */
		private LLIterator() {
			super();
			reset();
		}

		public void reset() {
			current = headNode_.next_;
		}

		/**
		 * true if the list has mew elements false otherwise
		 */
		public boolean hasNext() {
			return current != null && current != headNode_;
		}

		/**
		 * returns the next element in the list
		 */
		public T next() {
			T returnVal = current.data_;
			current = current.next_;
			return returnVal;
		}

		/**
		 * removes the the item the was last returned by next()
		 */
		public void remove() {
			LLNode toRemove = current.previous_;
			LLNode before = toRemove.previous_;
			attach(before, current);
			--length_;
			nodePool_.release(toRemove);
		}

		/**
		 * inserts an item into the list
		 * 
		 * @param toInsert
		 *            the item to insert
		 */
		public void insert(T toInsert) {
			LLNode newNode = nodePool_.allocate(toInsert);
			LLNode before = current.previous_;
			attach(before, newNode);
			attach(newNode, current);
			++length_;
		}
	}

	/**
	 * An Iterator to traverse the list from end to front
	 * 
	 * @author Matt Falkoski
	 */
	public class Reverse_LLIterator extends AllocationGuard implements Iterator<T> {
		private LLNode current = null;

		/**
		 * constructor
		 */
		private Reverse_LLIterator() {
			super();
			reset();
		}

		public void reset() {
			current = headNode_.previous_;
		}

		/**
		 * true if the list has mew elements false otherwise
		 */
		public boolean hasNext() {
			return current != null && current != headNode_;
		}

		/**
		 * returns the nest item in the list
		 */
		public T next() {
			T returnVal = current.data_;
			current = current.previous_;
			return returnVal;
		}

		/**
		 * removes the the item the was last returned by next()
		 */
		public void remove() {
			LLNode toRemove = current.next_;
			LLNode before = toRemove.next_;
			attach(current, before);
			--length_;
			nodePool_.release(toRemove);
		}

		/**
		 * inserts an item into the list
		 * 
		 * @param toInsert
		 *            the item to insert
		 */
		public void insert(T toInsert) {
			LLNode newNode = nodePool_.allocate(toInsert);
			LLNode before = current.next_;
			attach(newNode, before);
			attach(current, newNode);
			++length_;
		}
	}

	/*
	 * ====================================================================== ==
	 * Private Data Members
	 * =====================================================================
	 */
	/**
	 * <pre>
	 * the head node is a dummy that contains no data
	 * the next reference is the first item in the list
	 * the previous reference is the last item
	 * </pre>
	 */
	private LLNode headNode_ = null;
	/** the number of items in the list */
	private int length_ = 0;
	/** avoid allocation with pool */
	private LLNodePool nodePool_;
	
	private LLIterator iterator_;
	private Reverse_LLIterator reverseIterator_;

	private static final int DEFAULT_MAX_SIZE = 32;

} // end LList