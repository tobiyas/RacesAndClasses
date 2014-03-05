/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.ArrayList;
import java.util.List;

public class RotatableList<T> {

	/**
	 * The List of entries to store
	 */
	private final ArrayList<T> entries = new ArrayList<T>();

	/**
	 * The Current index selection of the List.
	 */
	private int currentIndex = 0;
	

	/**
	 * Sets the List of Entries.
	 * entries may be NULL.
	 * 
	 * WARNIG: The selection of the entries will be reset.
	 * 
	 * @param entries to set.
	 */
	public void setEntries(List<T> entries){
		currentIndex = 0;
		this.entries.clear();
		if(entries == null){
			return;
		}
		
		this.entries.addAll(entries);
	}
	
	
	/**
	 * Returns the next element in the Iterator.
	 * If the RotatableList is empty, null is returned.
	 * 
	 * @return the next entry.
	 */
	public T next(){
		if(entries.isEmpty()) return null; //no entries in list.
		
		currentIndex ++;
		if(currentIndex >= entries.size()){
			currentIndex = 0;
		}
		
		return entries.get(currentIndex);
	}
	
	
	/**
	 * Returns the current selected entry.
	 * If no entry is in list, null is returned.
	 * 
	 * @return
	 */
	public T currentEntry(){
		if(entries.isEmpty()) return null;
		return entries.get(currentIndex);
	}
	
	
	/**
	 * Returns a List of all Entries cached.
	 * HINT: can be empty if no entries present.
	 * 
	 * @return all entries
	 */
	public List<T> getAllEntries(){
		return new ArrayList<T>(entries);
	}

	
	/**
	 * Returns the size of the list
	 * 
	 * @return size of list
	 */
	public int size() {
		return entries != null ? entries.size() : 0;
	}


	/**
	 * Returns the Previous Element.
	 * 
	 * @return Previous Elemtent.
	 */
	public T previous() {
		if(entries.isEmpty()) return null;
		
		currentIndex --;
		if(currentIndex < 0){
			currentIndex = entries.size() - 1;
		}
		
		return entries.get(currentIndex);
	}
	
	
	
}
