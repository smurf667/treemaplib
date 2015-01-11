package de.engehausen.treemap;

import java.util.Iterator;

/**
 * An iterator that indicates the number of items it
 * will expose.
 * <br>When dealing with a huge input tree for the layout
 * calculation it can be beneficial to the peformance
 * to expose this iterator, because the internal lists
 * of the output model can be optimally sized using this
 * iterator.
 * @param <E> type of element the iterator exposes
 */
public interface IIteratorSize<E> extends Iterator<E> {

	/**
	 * Returns the number of elements the iterator holds.
	 * The return value may be an estimation; it may actually
	 * return more or less items.
	 * @return the estimated total number of items the iterator can return.
	 */
	int size();

}
