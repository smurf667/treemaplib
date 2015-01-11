package de.engehausen.treemap.impl;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.Node;

public class GenericTreeModelTest {

	/**
	 * Tests a new empty generic tree model.
	 */
	@Test
	public void testEmpty() {
		final ITreeModel<Node> model = new GenericTreeModel<Node>();
		Assert.assertNull(model.getRoot());
	}

	/**
	 * Tests a root-only generic tree model.
	 * @throws Exception in case of error.
	 */
	@Test
	public void testRoot() {
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 4711L);
		model.add(root, 4711L, null);
		Assert.assertSame(root, model.getRoot());
	}

	/**
	 * Tests a small generic tree model.
	 * @throws Exception in case of error.
	 */
	@Test
	public void testSmall() {
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 0L);
		final Node a = new Node("a", 1L);
		final Node b = new Node("b", 2L);
		model.add(root, 0L, null);
		model.add(a, 1L, root, true);
		model.add(b, 2L, root, true);

		Assert.assertTrue(model.hasChildren(root));
		Assert.assertFalse(model.hasChildren(a));
		Assert.assertFalse(model.hasChildren(b));
		Assert.assertSame(root, model.getParent(a));
		Assert.assertSame(root, model.getParent(b));

		Assert.assertEquals(3L, model.getWeight(root));
		Assert.assertEquals(1L, model.getWeight(a));
		Assert.assertEquals(2L, model.getWeight(b));

		final Iterator<Node> i = model.getChildren(root);
		Assert.assertTrue(i instanceof IIteratorSize<?>);
		Assert.assertEquals(2, ((IIteratorSize<?>) i).size());
		Assert.assertSame(a, i.next());
		Assert.assertSame(b, i.next());
	}

	/**
	 * Test a chain (root-a-b-c) hierarchy in the generic tree model.
	 * @throws Exception in case of error.
	 */
	@Test
	public void testChain() {
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 0L);
		final Node a = new Node("a", 1L);
		final Node b = new Node("b", 1L);
		final Node c = new Node("c", 1L);
		model.add(root, 0L, null);
		model.add(a, 1L, root, true);
		model.add(b, 1L, a, true);
		model.add(c, 1L, b, true);
		Assert.assertEquals(3, model.getWeight(root));
		Assert.assertEquals(3, model.getWeight(a));
		Assert.assertEquals(2, model.getWeight(b));
		Assert.assertEquals(1, model.getWeight(c));
	}

}
