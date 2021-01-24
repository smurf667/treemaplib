package de.engehausen.treemap.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.Node;
import de.engehausen.treemap.NodeEx;

public class GenericTreeModelExTest {

	/**
	 * Tests a new empty generic tree model.
	 */
	@Test
	public void testEmpty() {
		final ITreeModel<Node> model = new GenericTreeModelEx<Node, BigDecimal>(DefaultArithmetics.bigDecimals());
		Assert.assertNull(model.getRoot());
	}

	/**
	 * Tests a root-only generic tree model.
	 */
	@Test
	public void testRoot() {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final BigDecimal number = BigDecimal.valueOf(127);
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", number);
		model.add(root, number, null);
		Assert.assertSame(root, model.getRoot());
	}

	/**
	 * Tests a small generic tree model.
	 */
	@Test
	public void testSmall() {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", BigDecimal.ZERO);
		final NodeEx<BigDecimal> a = new NodeEx<BigDecimal>("a", BigDecimal.ONE);
		final BigDecimal two = BigDecimal.valueOf(2L);
		final NodeEx<BigDecimal> b = new NodeEx<BigDecimal>("b", two);
		model.add(root, BigDecimal.ZERO, null);
		model.add(a, BigDecimal.ONE, root, true);
		model.add(b, two, root, true);

		Assert.assertTrue(model.hasChildren(root));
		Assert.assertFalse(model.hasChildren(a));
		Assert.assertFalse(model.hasChildren(b));
		Assert.assertSame(root, model.getParent(a));
		Assert.assertSame(root, model.getParent(b));

		Assert.assertEquals(BigDecimal.valueOf(3L), model.getWeight(root));
		Assert.assertEquals(BigDecimal.ONE, model.getWeight(a));
		Assert.assertEquals(two, model.getWeight(b));

		final Iterator<NodeEx<BigDecimal>> i = model.getChildren(root);
		Assert.assertTrue(i instanceof IIteratorSize<?>);
		Assert.assertEquals(2, ((IIteratorSize<?>) i).size());
		Assert.assertSame(a, i.next());
		Assert.assertSame(b, i.next());
	}

	/**
	 * Test a chain (root-a-b-c) hierarchy in the generic tree model.
	 */
	@Test
	public void testChain() {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", BigDecimal.ZERO);
		final NodeEx<BigDecimal> a = new NodeEx<BigDecimal>("a", BigDecimal.ONE);
		final NodeEx<BigDecimal> b = new NodeEx<BigDecimal>("b", BigDecimal.ONE);
		final NodeEx<BigDecimal> c = new NodeEx<BigDecimal>("c", BigDecimal.ONE);
		model.add(root, BigDecimal.ZERO, null);
		model.add(a, BigDecimal.ONE, root, true);
		model.add(b, BigDecimal.ONE, a, true);
		model.add(c, BigDecimal.ONE, b, true);
		Assert.assertEquals(BigDecimal.valueOf(3L), model.getWeight(root));
		Assert.assertEquals(BigDecimal.valueOf(3L), model.getWeight(a));
		Assert.assertEquals(BigDecimal.valueOf(2L), model.getWeight(b));
		Assert.assertEquals(BigDecimal.ONE, model.getWeight(c));
	}

}
