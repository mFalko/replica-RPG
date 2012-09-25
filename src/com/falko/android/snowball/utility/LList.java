/*
 * LList.java
 */
package com.falko.android.snowball.utility;

import java.util.Iterator;

/**
 * LList<T>
 * </br>
 * This Class contains the definition of a 
 * circular doubly linked list
 * </br>
 * @author Matt Falkoski
 * @version 1.1
 *
 *  Compiler: Java 1.6 <br>
 *  Hardware: PC<br>
 *
 * May 4, 2010 <br>
 * MF completed v 1.0
 * 
 * Sep 20, 2012 <br>
 * MF add in node pool
 */
 
public class LList<T> implements ListInterface<T> ,Iterable<T>{
/*====================================================================== 
== Public Interface
======================================================================*/
	/**
	 * Default constructor </br>
	 * Creates a new list with capacity 32
	 */
	public LList() {
		this(DEFAULT_MAX_SIZE);
	}
	
	/**
	 * Constructor </br>
	 * Creates a new list with capacity size
	 * 
	 * @param size  the max capacity of the list
	 */
	public LList(int size) {
		MAX_SIZE = size;
		nodePool_ = new NodePool(MAX_SIZE);
		headNode_ = nodePool_.allocate();
		length_   = 0;
	}
	
	public final void clear() {
		if (isEmpty()) return;
		Node temp = headNode_;
		Node next = null;
		while ((next = temp.next()) != headNode_) {
			nodePool_.release(temp);
			temp = next;
		}
		length_ = 0;
	} 

	public int getLength() {
		return length_;
	}

	public boolean add(T newEntry) {
		Node newNode = nodePool_.allocate(newEntry);
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
		if (newPosition >= 0 && newPosition < length_) {
			Node newNode = nodePool_.allocate(newEntry);
			if (newPosition == 0) {
				attach(newNode, headNode_.next());
				attach(headNode_, newNode);
			} else {
				Node nodeBefore = getNodeAt(newPosition - 1);
				Node nodeAfter = nodeBefore.next();
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
			assert headNode_.next()     == null 
				&& headNode_.previous() == null;
			return true;
		} else {
			assert headNode_.next()     != null 
				&& headNode_.previous() != null;
			return false;
		} 
	} 

	public boolean replace(int givenPosition, T newEntry) {
		if (givenPosition >= 0 && givenPosition < length_ && !isEmpty()) {
			Node desiredNode = getNodeAt(givenPosition);
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
			Node nodeToRemove = getNodeAt(givenPosition);
			result = nodeToRemove.data();
			attach(nodeToRemove.previous(), nodeToRemove.next());
			nodePool_.release(nodeToRemove);
			length_--;
		} 
		return result; // return removed entry, or
						// null if operation fails
	}

	public boolean remove(T toRemove)
	{
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
		return MAX_SIZE == length_;
	}

	public boolean contains(T anEntry) {
		if (isEmpty()) return false;
		Node currentNode = headNode_.next();
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
		return new LLIterator();
	}

	/**
	 * @return a Reverse Iterator over the list
	 */
	public Iterator<T> rBegin() {
		return new Reverse_LLIterator();
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
	protected void finalize() throws Throwable{
		super.finalize();
		clear();
		nodePool_.release(headNode_);
	}
/*======================================================================
== Private methods
======================================================================*/
	/**
	 * returns the node at the given position
	 * @param givenPosition the position on the list
	 */
	private Node getNodeAt(int givenPosition) {
		assert !isEmpty() 
			&& (givenPosition < 0 || givenPosition >= length_);
		
		Node currentNode = headNode_.next();
		for (int counter = 0; counter < givenPosition; counter++)
			currentNode = currentNode.next();
		assert currentNode != headNode_;
		return currentNode;
	} 

	/**
	 * attaches two nodes together
	 * @param first  the item before second
	 * @param second the item after first
	 */
	private void attach(Node first, Node second) {
		first.setNext(second);
		second.setPrevious(first);
	}

	/**
	 * A Node class for the list
	 * @author Matt Falkoski
	 */
	private class Node {

		private T data_; 
		private Node next_; 
		private Node previous_; 

		/**
		 * 
		 */
		private Node() {
			data_ = null;
			next_ = null;
			previous_ = null;
		} 

		/**
		 * 
		 * @param data
		 */
		private Node(T data) {
			this();
			data_ = data;
		}
		
		private void reset() {
			data_ = null;
			next_ = null;
			previous_ = null;
		}
	
		private Node next()     { return next_;     }
		private Node previous() { return previous_; }
		private T    data()     { return data_;     }
		
		private void setNext(Node n)      { next_     = n; }
		private void setPrevious(Node p)  { previous_ = p; }
		private void setData(T d)         { data_     = d; }
	}

	/**
	 * 
	 * @author matt
	 *
	 */
	private class NodePool extends TObjectPool<Node> {

		private NodePool(int size) {
			super(size);
		}
		
		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
                getAvailable().add(new Node());
            }
		}
		
		public Node allocate(T data) {
	       Node newNode = super.allocate();        
	       newNode.setData(data);
	       return newNode;
	    }
		
		public void release(Object entry) {
            @SuppressWarnings("unchecked")
			Node node = (Node)entry;
            node.reset();
            super.release(node);
        }
		
	}
	
	/**
	 * An Iterator to traverse the list
	 * @author Matt Falkoski
	 */
	public class LLIterator implements Iterator<T> {
		private Node current = null;

		/**
 		 * Constructor
 		 */
		private LLIterator() {
			current = headNode_.next_;
		}

		/**
 		 * true if the list has mew elements
 		 * false otherwise
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
			Node toRemove = current.previous_;
			Node before = toRemove.previous_;
			attach(before, current);
			--length_;
			nodePool_.release(toRemove);
		}

		/**
		 * inserts an item into the list
		 * @param toInsert the item to insert
		 */
		public void insert(T toInsert) {
			Node newNode = nodePool_.allocate(toInsert);
			Node before = current.previous_;
			attach(before, newNode);
			attach(newNode, current);
			++length_;
		}
	}

	/**
	 * An Iterator to traverse the list from end to front
	 * @author Matt Falkoski
	 */
	public class Reverse_LLIterator implements Iterator<T> {
		private Node current = null;

		/**
 		 * constructor
 		 */
		private Reverse_LLIterator() {
			current = headNode_.previous_;
		}

		/**
 		 * true if the list has mew elements
 		 * false otherwise
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
			Node toRemove = current.next_;
			Node before = toRemove.next_;
			attach(current, before);
			--length_;
			nodePool_.release(toRemove);
		}
		
		/**
		 * inserts an item into the list
		 * @param toInsert the item to insert
		 */
		public void insert(T toInsert) {
			Node newNode = nodePool_.allocate(toInsert);
			Node before = current.next_;
			attach(newNode, before);
			attach(current, newNode);
			++length_;
		}
	}

/*======================================================================
 == Private Data Members
 =====================================================================*/
	/** <pre>
	 * the head node is a dummy that contains no data
	 * the next reference is the first item in the list
	 * the previous reference is the last item 
	 * </pre>
	 */
	private Node headNode_ = null;
	/** the number of items in the list */
	private int   length_  = 0;
	/** avoid allocation with pool */
	private NodePool nodePool_;
	
	private final int MAX_SIZE;
	private static final int DEFAULT_MAX_SIZE = 32;
	
} // end LList