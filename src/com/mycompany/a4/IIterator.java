package com.mycompany.a4;



/**
 * IIterator is an interface for iterator objects used by collection objects.
 * It follows the iterator design pattern.
 * 
 * @author Eric Brown
 */
public interface IIterator {
	
	/**
	 * @return				true if there is another object yet to be processed within the collection
	 */
	public abstract boolean hasNext();
	
	/**
	 * Retrieves the next object in the collection to be processed, if there is one.
	 * 
	 * @return				the next object in the collection
	 */
	public abstract GameObject getNext();
}
