package de.engehausen.treemap.impl;

import java.math.BigDecimal;

import de.engehausen.treemap.NumberArithmetic;

/**
 * Default implementations for arithmetics of common number types.
 */
public class DefaultArithmetics {

	/**
	 * Returns number arithmetics for {@link BigDecimal}.
	 * @return number arithmetics for {@link BigDecimal}.
	 */
	public static NumberArithmetic<BigDecimal> bigDecimals() {
		return BigDecimalArithmetics.INSTANCE;
	}

	/**
	 * Returns number arithmetics for {@link Double}.
	 * @return number arithmetics for {@link Double}.
	 */
	public static NumberArithmetic<Double> doubles() {
		return DoubleArithmetics.INSTANCE;
	}

	protected DefaultArithmetics() {
		// no to be directly instantiated
	}

	protected static class BigDecimalArithmetics implements NumberArithmetic<BigDecimal> {
		protected static final NumberArithmetic<BigDecimal> INSTANCE = new BigDecimalArithmetics();
		@Override
		public BigDecimal zero() {
			return BigDecimal.ZERO;
		}
		@Override
		public BigDecimal add(final BigDecimal op1, final BigDecimal op2) {
			return op1.add(op2);
		}
		@Override
		public BigDecimal sub(final BigDecimal op1, final BigDecimal op2) {
			return op1.subtract(op2);
		}
	}

	protected static class DoubleArithmetics implements NumberArithmetic<Double> {
		protected static final NumberArithmetic<Double> INSTANCE = new DoubleArithmetics();
		@Override
		public Double zero() {
			return Double.valueOf(0);
		}
		@Override
		public Double add(final Double op1, final Double op2) {
			return Double.valueOf(op1.doubleValue() + op2.doubleValue());
		}
		@Override
		public Double sub(final Double op1, final Double op2) {
			return Double.valueOf(op1.doubleValue() - op2.doubleValue());
		}
	}

}
