/**
 * ListInterface.java
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


/**
 * LList<T>
 * </br>
 * An interface for the ADT list. Entries in
 * the list have positions that begin with 0.
 * </br>
 * @author Matt Falkoski
 * @version 1.0
 *
 *  Compiler: Java 1.6 <br>
 *  Hardware: PC<br>
 *
 * May 4, 2010 <br>
 * MF completed v 1.0
 */
public interface ListInterface<T> {
	/**
	 * Task: Adds a new entry to the end of the list. Entries currently in the
	 * list are unaffected. The lists size is increased by 1.
	 * 
	 * @param newEntry
	 *            the object to be added as a new entry
	 * @return true if the addition is successful, or false if the list is full
	 */
	public boolean add(T newEntry);

	/**
	 * Task: Adds a new entry at a specified position within the list. Entries
	 * originally at and above the specified position are at the next higher
	 * position within the list. The lists size is increased by 1.
	 * 
	 * @param newPosition
	 *            an integer that specifies the desired position of the new
	 *            entry
	 * @param newEntry
	 *            the object to be added as a new entry
	 * @return true if the addition is successful, or false if either the list
	 *         is full, newPosition < 1, or newPosition > getLength()+1
	 */
	public boolean add(int newPosition, T newEntry);

	/**
	 * Task: Removes the entry at a given position from the list. Entries
	 * originally at positions higher than the given position are at the next
	 * lower position within the list, and the lists size is decreased by 1.
	 * 
	 * @param givenPosition
	 *            an integer that indicates the position of the entry to be
	 *            removed
	 * @return a reference to the removed entry or null, if either the list was
	 *         empty, givenPosition < 1, or givenPosition > getLength()
	 */
	public T remove(int givenPosition);

	/**
	 * Task: Removes the given entry from the list. Entries originally at 
	 * positions higher than the given position are at the next
	 * lower position within the list, and the lists size is decreased by 1.
	 * 
	 * @param toRemove
	 *            an item to remove from the list
	 * @return true if the item was removed, false otherwise
	 */
	public boolean remove(T toRemove);

	/** Task: Removes all entries from the list. */
	public void clear();

	/**
	 * Task: Replaces the entry at a given position in the list.
	 * 
	 * @param givenPosition
	 *            an integer that indicates the position of the entry to be
	 *            replaced
	 * @param newEntry
	 *            the object that will replace the entry at the position
	 *            givenPosition
	 * @return true if the replacement occurs, or false if either the list is
	 *         empty, givenPosition < 1, or givenPosition > getLength()
	 */
	public boolean replace(int givenPosition, T newEntry);

	/**
	 * Task: Retrieves the entry at a given position in the list.
	 * 
	 * @param givenPosition
	 *            an integer that indicates the position of the desired entry
	 * @return a reference to the indicated entry or null, if either the list is
	 *         empty, givenPosition < 1, or givenPosition > getLength()
	 */
	public T getEntry(int givenPosition);

	/**
	 * Task: Sees whether the list contains a given entry.
	 * 
	 * @param anEntry
	 *            the object that is the desired entry
	 * @return true if the list contains anEntry, or false if not
	 */
	public boolean contains(T anEntry);

	/**
	 * Task: Gets the length of the list.
	 * 
	 * @return the integer number of entries currently in the list
	 */
	public int getLength();

	/**
	 * Task: Sees whether the list is empty.
	 * 
	 * @return true if the list is empty, or false if not
	 */
	public boolean isEmpty();

	/**
	 * Task: Sees whether the list is full.
	 * 
	 * @return true if the list is full, or false if not
	 */
	public boolean isFull();

	/**
	 * Task: Displays all entries that are in the list, one per line, in the
	 * order in which they occur in the list.
	 */
	public void display();

	/**
	 * Task: Displays all entries that are in the list, one per line, in the
	 * reverse order in which they occur in the list.
	 */
	public void rDisplay();
}