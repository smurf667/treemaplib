package de.engehausen.treemap.impl;

import java.math.BigDecimal;

import org.junit.Assert;

import junit.framework.TestCase;
import de.engehausen.treemap.NumberArithmetic;

public class DefaultArithmeticsTest extends TestCase {

	public void testDouble() {
		perform(DefaultArithmetics.doubles(), Double.valueOf(1L), Double.valueOf(2L));
	}

	public void testBigDecimal() {
		perform(DefaultArithmetics.bigDecimals(), BigDecimal.ONE, BigDecimal.valueOf(2L));
	}

	private <T extends Number> void perform(final NumberArithmetic<T> arithmetic, final T one, final T two) {
		final T zero = arithmetic.zero();
		Assert.assertEquals("must be zero", zero, arithmetic.add(zero, zero));
		Assert.assertEquals("must be zero", zero, arithmetic.sub(zero, zero));
		Assert.assertEquals("must be one", one, arithmetic.add(zero, one));
		Assert.assertEquals("must be one", one, arithmetic.sub(one, zero));
		Assert.assertEquals("must be one", one, arithmetic.sub(two, one));
		Assert.assertEquals("must be two", two, arithmetic.add(one, one));
	}

}
