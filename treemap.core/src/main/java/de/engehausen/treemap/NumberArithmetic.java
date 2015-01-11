package de.engehausen.treemap;

/**
 * Generic number arithmetic for {@link IGenericWeightedTreeModel} instances.
 * @param <T> the type of weight
 */
public interface NumberArithmetic<T extends Number> {
	/**
	 * Returns the zero instance of the type.
	 * @return the zero instance, never <code>null</code>.
	 */
	T zero();
	/**
	 * Returns the sum of the two numbers.
	 * @param op1 the first operand, must not be <code>null</code>
	 * @param op2 the second operand, must not be <code>null</code>
	 * @return the sum of the two numbers, never <code>null</code>.
	 */
	T add(T op1, T op2);
	/**
	 * Returns the difference of the two numbers.
	 * @param op1 the first operand, must not be <code>null</code>
	 * @param op2 the second operand, must not be <code>null</code>
	 * @return the difference of the two numbers, never <code>null</code>.
	 */
	T sub(T op1, T op2);
}