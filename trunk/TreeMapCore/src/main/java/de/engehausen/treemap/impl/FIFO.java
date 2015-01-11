package de.engehausen.treemap.impl;

/**
 * A fast FIFO implementation.
 * @param <T> the type of object the queue holds.
 */
public class FIFO<T> {

	protected Entry<T> head;
	protected Entry<T> tail;

	/**
	 * Removes the head object from the queue.
	 * @return the head object (may be <code>null</code>).
	 */
	public T pull() {
		if (head != null) {
			final Entry<T> current = head;
			head = head.next;
			if (head == null) {
				tail = null;
			}
			return current.value;
		} else {
			return null;
		}
	}

	/**
	 * Appends the given object to the end of the queue.
	 * @param value the object to add
	 */
	public void push(final T value) {
		if (tail != null) {
			final Entry<T> current = new Entry<T>(value);
			tail.next = current;
			tail = current;
		} else {
			final Entry<T> current = new Entry<T>(value);
			head = tail = current;
		}
	}

	/**
	 * Returns if the queue is empty or not.
	 * @return <code>true</code> if there are elements in the
	 * queue, <code>false</code> otherwise.
	 */
	public boolean notEmpty() {
		return head != null;
	}

	/**
	 * Queue entries implementation.
	 * @param <T> the type of node the entry holds
	 */
	private static final class Entry<T> {
		protected final T value;
		protected Entry<T> next;

		public Entry(final T aValue) {
			value = aValue;
		}
	}

}
