package de.engehausen.treemap;

import java.math.BigDecimal;

/**
 * An extension to the weighted tree model which supports arbitrary number-based weight types.
 * This may be used where precision is needed that <code>long</code> cannot provide,
 * e.g. by using {@link BigDecimal}. Warning: For huge models, using objects instead of
 * the <code>long</code> primitive data type can be detrimental to performance.
 *
 * @param <N> the type of node returned by model.
 * @param <T> the weight type.
 */
public interface IGenericWeightedTreeModel<N, T extends Number> extends ITreeModel<N> {

	/**
	 * The precise weight of the node.
	 * @param node the node for which to return the weight.
	 * @return the weight of the node, or a value representing zero if the node is
	 * not known. Never returns <code>null</code>.
	 */
	T getWeight(N node);

	/**
	 * Returns the number arithmetic instance for the model.
	 * @return the number arithmetic instance for the model, never <code>null</code>.
	 */
	NumberArithmetic<T> getArithmetic();

}
