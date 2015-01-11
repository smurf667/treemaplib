package de.engehausen.treemap.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import junit.framework.TestCase;
import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.Node;
import de.engehausen.treemap.NodeEx;

public class GenericTreeModelExTest extends TestCase {

	/**
	 * Tests a new empty generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testEmpty() throws Exception {
		final ITreeModel<Node> model = new GenericTreeModelEx<Node, BigDecimal>(DefaultArithmetics.bigDecimals());
		assertNull(model.getRoot());
	}

	/**
	 * Tests a root-only generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testRoot() throws Exception {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final BigDecimal number = BigDecimal.valueOf(127);
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", number);
		model.add(root, number, null);
		assertSame(root, model.getRoot());
	}

	/**
	 * Tests a small generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testSmall() throws Exception {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", BigDecimal.ZERO);
		final NodeEx<BigDecimal> a = new NodeEx<BigDecimal>("a", BigDecimal.ONE);
		final BigDecimal two = BigDecimal.valueOf(2L);
		final NodeEx<BigDecimal> b = new NodeEx<BigDecimal>("b", two);
		model.add(root, BigDecimal.ZERO, null);
		model.add(a, BigDecimal.ONE, root, true);
		model.add(b, two, root, true);

		assertTrue(model.hasChildren(root));
		assertFalse(model.hasChildren(a));
		assertFalse(model.hasChildren(b));
		assertSame(root, model.getParent(a));
		assertSame(root, model.getParent(b));

		assertEquals(BigDecimal.valueOf(3L), model.getWeight(root));
		assertEquals(BigDecimal.ONE, model.getWeight(a));
		assertEquals(two, model.getWeight(b));

		final Iterator<NodeEx<BigDecimal>> i = model.getChildren(root);
		assertTrue(i instanceof IIteratorSize<?>);
		assertEquals(2, ((IIteratorSize<?>) i).size());
		assertSame(a, i.next());
		assertSame(b, i.next());
	}

	/**
	 * Test a chain (root-a-b-c) hierarchy in the generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testChain() throws Exception {
		final GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal> model = new GenericTreeModelEx<NodeEx<BigDecimal>, BigDecimal>(DefaultArithmetics.bigDecimals());
		final NodeEx<BigDecimal> root = new NodeEx<BigDecimal>("root", BigDecimal.ZERO);
		final NodeEx<BigDecimal> a = new NodeEx<BigDecimal>("a", BigDecimal.ONE);
		final NodeEx<BigDecimal> b = new NodeEx<BigDecimal>("b", BigDecimal.ONE);
		final NodeEx<BigDecimal> c = new NodeEx<BigDecimal>("c", BigDecimal.ONE);
		model.add(root, BigDecimal.ZERO, null);
		model.add(a, BigDecimal.ONE, root, true);
		model.add(b, BigDecimal.ONE, a, true);
		model.add(c, BigDecimal.ONE, b, true);
		assertEquals(BigDecimal.valueOf(3L), model.getWeight(root));
		assertEquals(BigDecimal.valueOf(3L), model.getWeight(a));
		assertEquals(BigDecimal.valueOf(2L), model.getWeight(b));
		assertEquals(BigDecimal.ONE, model.getWeight(c));
	}

}
