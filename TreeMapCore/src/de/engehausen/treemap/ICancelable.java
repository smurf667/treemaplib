package de.engehausen.treemap;

/**
 * State indicator for an operation. This can be used
 * to query if an operation is considered canceled or not.
 */
public interface ICancelable {

	/**
	 * Returns cancellation state.
	 * @return <code>true</code> if the operation
	 * is canceled, <code>false</code> otherwise.
	 */
	boolean isCanceled();

}
