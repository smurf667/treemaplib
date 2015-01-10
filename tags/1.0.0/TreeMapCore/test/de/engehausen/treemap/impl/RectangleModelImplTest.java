package de.engehausen.treemap.impl;

import java.util.Iterator;

import de.engehausen.treemap.IRectangle;
import junit.framework.TestCase;

public class RectangleModelImplTest extends TestCase {

	public void testEmpty() throws Exception {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>();
		assertNull(model.getRoot());
	}

	public void testRoot() throws Exception {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>(new RectangleImpl<String>("hello", 0, 0, 0, 0));
		final IRectangle<String> root = model.getRoot();
		assertNotNull(root);
		assertEquals("hello", root.getNode());
		assertNull(model.getParent(root));
	}

	public void testChildren1() throws Exception {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>(new RectangleImpl<String>("hello", 0, 0, 0, 0));
		final IRectangle<String> root = model.getRoot();
		final RectangleImpl<String> child = new RectangleImpl<String>("child", 0, 0, 0, 0);
		model.addChild(root, child);
		assertTrue(model.hasChildren(root));
		final Iterator<IRectangle<String>> it = model.getChildren(root);
		assertNotNull(it);
		assertTrue(it.hasNext());
		final IRectangle<String> c = it.next();
		assertNotNull(c);
		assertFalse(it.hasNext());
		assertFalse(model.hasChildren(c));
		assertSame(root, model.getParent(c));
	}

}
