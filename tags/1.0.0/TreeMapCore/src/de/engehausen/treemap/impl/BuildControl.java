package de.engehausen.treemap.impl;

import de.engehausen.treemap.ICancelable;

/**
 * Build control object which show the state of an operation
 * (canceled/not canceled) and which also allows to cancel
 * the operation.
 */
public class BuildControl implements ICancelable {

	private volatile boolean flag;

	/**
	 * Creates the build control object.
	 */
	public BuildControl() {
		flag = false;
	}

	@Override
	public boolean isCanceled() {
		return flag;
	}

	/**
	 * Sets the state of the build control to "canceled",
	 * thereby allowing the operation to be canceled.
	 */
	public void cancel() {
		flag = true;
	}

}
