package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RotatableList<T> {

	/**
	 * The List of entries to store
	 */
	private List<T> entries = new LinkedList<T>();
	
	/**
	 * The iterator iterating over the entries.
	 * Has the currentSelection item selected.
	 */
	private Iterator<T> entryIterator = entries.iterator();
	
	/**
	 * The entry selected
	 */
	private T currentSelection = null;
	

	/**
	 * Sets the List of Entries.
	 * entries may be NULL.
	 * 
	 * WARNIG: The selection of the entries will be reset.
	 * 
	 * @param entries to set.
	 */
	public void setEntries(List<T> entries){
		if(entries == null){
			entries = new LinkedList<T>();
		}
		
		this.entries = entries;
		this.entryIterator = entries.iterator();
		
		this.currentSelection = next();
	}
	
	
	/**
	 * Returns the next element in the Iterator.
	 * If the RotatableList is empty, null is returned.
	 * 
	 * @return the next entry.
	 */
	public T next(){
		if(entries.isEmpty()) return null; //no entries in list.
		
		if(!entryIterator.hasNext()){
			entryIterator = entries.iterator();
		}
		
		currentSelection = entryIterator.next();
		return currentSelection;
	}
	
	
	/**
	 * Returns the current selected entry.
	 * If no entry is in list, null is returned.
	 * 
	 * @return
	 */
	public T currentEntry(){
		return this.currentSelection;
	}
	
	
	/**
	 * Returns a List of all Entries cached.
	 * HINT: can be empty if no entries present.
	 * 
	 * @return all entries
	 */
	public List<T> getAllEntries(){
		return entries;
	}
}
