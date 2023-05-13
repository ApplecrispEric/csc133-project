package com.mycompany.a4;

import java.util.Vector;


/**
 * GameObjectCollection represents a collection of game objects and exposes methods
 * to add objects to the collection as well as obtain an iterator for iteration over the objects.
 * 
 * @author Eric Brown
 */
public class GameObjectCollection implements ICollection {
	private Vector<GameObject> collection = new Vector<GameObject>();
	
	/**
	 * Returns an iterator to iterate over the objects in the collection.
	 * 
	 * @return					collection iterator
	 */
	public IIterator getIterator() {
		return new ObjectIterator();
	}
	
	/**
	 * Adds an object to the collection.
	 * 
	 * @param object			the object to add to the collection
	 */
	@Override
	public void add(GameObject object) {
		collection.add(object);
		
	}
	
	/**
	 * Removes all the objects from the collection.
	 */
	@Override
	public void clear() {
		collection.clear();
	}
	
	/**
	 * Removes an object from the collection.
	 * 
	 * @param object		the object to remove from the collection
	 */
	@Override
	public void remove(GameObject object) {
		collection.remove(object);
	}
	
	/**
	 * Private inner class that represents the iterator object that the outer class returns.
	 * 
	 * @author Eric Brown
	 */
	private class ObjectIterator implements IIterator {
		private int index = 0;
		
		/**
		 * @return 				true if there is another object in the collection to iterate over still, false otherwise
		 */
		@Override
		public boolean hasNext( ) {
			return index < collection.size();
		}
		
		/**
		 * @return				the next object in the sequence
		 */
		@Override
		public GameObject getNext() {
			if (this.hasNext()) {
				return collection.elementAt(this.index++);
			}
			
			return null;
		}
	}
}
