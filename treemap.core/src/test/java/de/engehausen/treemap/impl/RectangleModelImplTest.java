package de.engehausen.treemap.impl;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.treemap.IRectangle;

public class RectangleModelImplTest {

	@Test
	public void testEmpty() {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>();
		Assert.assertNull(model.getRoot());
	}

	@Test
	public void testRoot() {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>(new RectangleImpl<String>("hello", 0, 0, 0, 0));
		final IRectangle<String> root = model.getRoot();
		Assert.assertNotNull(root);
		Assert.assertEquals("hello", root.getNode());
		Assert.assertNull(model.getParent(root));
	}

	@Test
	public void testChildren1() {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>(new RectangleImpl<String>("hello", 0, 0, 0, 0));
		final IRectangle<String> root = model.getRoot();
		final RectangleImpl<String> child = new RectangleImpl<String>("child", 0, 0, 0, 0);
		model.addChild(root, child);
		Assert.assertTrue(model.hasChildren(root));
		final Iterator<IRectangle<String>> it = model.getChildren(root);
		Assert.assertNotNull(it);
		Assert.assertTrue(it.hasNext());
		final IRectangle<String> c = it.next();
		Assert.assertNotNull(c);
		Assert.assertFalse(it.hasNext());
		Assert.assertFalse(model.hasChildren(c));
		Assert.assertSame(root, model.getParent(c));
	}

	@Test
	public void testToString() {
		final RectangleModelImpl<String> model = new RectangleModelImpl<String>(new RectangleImpl<String>("hello", 0, 0, 0, 0));
		Assert.assertTrue(model.toString().contains("hello"));
	}

}
