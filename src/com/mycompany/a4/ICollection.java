package com.mycompany.a4;


/**
 * Interface defined for the iterator design pattern. Implementations of this interface
 * represent collections of objects that can be iterated over with an iterator retrieved from
 * a call to getIterator().
 * 
 * @author Eric Brown
 */
public interface ICollection {
	
	/**
	 * Adds an object to the collection.
	 * 
	 * @param object		the object to add
	 */
	public void add(GameObject object);
	
	/**
	 * Removes an object from the collection.
	 * 
	 * @param object		the object to remove from the collection
	 */
	public void remove(GameObject object);
	
	/**
	 * Removes all objects from the collection.
	 */
	public void clear();
	
	/**
	 * Retrieves an iterator for the collection.
	 * 
	 * @return				iterator for the collection
	 */
	public IIterator getIterator();
}
