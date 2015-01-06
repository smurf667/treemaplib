package de.engehausen.treemap.impl;

import java.util.Iterator;

import junit.framework.TestCase;
import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.Node;

public class GenericTreeModelTest extends TestCase {

	/**
	 * Tests a new empty generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testEmpty() throws Exception {
		final ITreeModel<Node> model = new GenericTreeModel<Node>();
		assertNull(model.getRoot());
	}

	/**
	 * Tests a root-only generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testRoot() throws Exception {
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 4711L);
		model.add(root, 4711L, null);
		assertSame(root, model.getRoot());
	}

	/**
	 * Tests a small generic tree model.
	 * @throws Exception in case of error.
	 */
	public void testSmall() throws Exception {
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 0L);
		final Node a = new Node("a", 1L);
		final Node b = new Node("b", 2L);
		model.add(root, 0L, null);
		model.add(a, 1L, root, true);
		model.add(b, 2L, root, true);
		
		assertTrue(model.hasChildren(root));
		assertFalse(model.hasChildren(a));
		assertFalse(model.hasChildren(b));
		assertSame(root, model.getParent(a));
		assertSame(root, model.getParent(b));
		
		assertEquals(3L, model.getWeight(root));
		assertEquals(1L, model.getWeight(a));
		assertEquals(2L, model.getWeight(b));
		
		final Iterator<Node> i = model.getChildren(root);
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
		final GenericTreeModel<Node> model = new GenericTreeModel<Node>();
		final Node root = new Node("root", 0L);
		final Node a = new Node("a", 1L);
		final Node b = new Node("b", 1L);
		final Node c = new Node("c", 1L);
		model.add(root, 0L, null);
		model.add(a, 1L, root, true);
		model.add(b, 1L, a, true);
		model.add(c, 1L, b, true);
		assertEquals(3, model.getWeight(root));
		assertEquals(3, model.getWeight(a));
		assertEquals(2, model.getWeight(b));
		assertEquals(1, model.getWeight(c));
	}

}
