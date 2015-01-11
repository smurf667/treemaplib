package de.engehausen.treemap.impl;

import org.junit.Assert;
import org.junit.Test;

public class RectangleImplTest {

	@Test
	public void testSimple() {
		final RectangleImpl<String> rect = new RectangleImpl<String>(null, 4, 8, 16, 32);
		Assert.assertEquals(4, rect.getX());
		Assert.assertEquals(8, rect.getY());
		Assert.assertEquals(16, rect.getWidth());
		Assert.assertEquals(32, rect.getHeight());
		final int[][] coords = {
				{ 0, 0 }, { 12, 0 }, { 100, 0 },
				{ 0, 10 }, /*the only one inside the rect*/{ 12, 10 }, { 100, 10 },
				{ 0, 50 }, { 12, 50 }, { 100, 50 },
		};
		final boolean[] expected = {
			false, false, false,
			false, true, false,
			false, false, false
		};
		for (int i = expected.length-1; i >= 0; i--) {
			Assert.assertTrue(expected[i] == rect.contains(coords[i][0], coords[i][1]));
		}
	}

	@Test
	public void testCompare() {
		final RectangleImpl<String> a = new RectangleImpl<String>("a", 4, 8, 16, 32);
		final RectangleImpl<String> b = new RectangleImpl<String>("a", 4, 8, 16, 32);
		final RectangleImpl<String> c = new RectangleImpl<String>("c", 4, 8, 32, 16);
		Assert.assertEquals(a, b);
		Assert.assertEquals(b, a);
		Assert.assertFalse(a.equals(c));
		Assert.assertFalse(c.equals(a));
	}

	@Test
	public void testSplitHorizontally() {
		final RectangleImpl<String> a = new RectangleImpl<String>("a", 0, 0, 16, 32);
		RectangleImpl<String>[] sub = a.split(0.5);
		Assert.assertNotNull(sub);
		Assert.assertEquals(2, sub.length);
		Assert.assertEquals(0, sub[0].getX());
		Assert.assertEquals(0, sub[0].getY());
		Assert.assertEquals(16, sub[0].getWidth());
		Assert.assertEquals(16, sub[0].getHeight());
		Assert.assertEquals(0, sub[1].getX());
		Assert.assertEquals(16, sub[1].getY());
		Assert.assertEquals(16, sub[1].getWidth());
		Assert.assertEquals(16, sub[1].getHeight());
	}

	@Test
	public void testSplitVertically() {
		final RectangleImpl<String> a = new RectangleImpl<String>("hello world", 0, 0, 32, 16);
		RectangleImpl<String>[] sub = a.split(0.5);
		Assert.assertNotNull(sub);
		Assert.assertEquals(2, sub.length);
		Assert.assertEquals(0, sub[0].getX());
		Assert.assertEquals(0, sub[0].getY());
		Assert.assertEquals(16, sub[0].getWidth());
		Assert.assertEquals(16, sub[0].getHeight());
		Assert.assertEquals(16, sub[1].getX());
		Assert.assertEquals(0, sub[1].getY());
		Assert.assertEquals(16, sub[1].getWidth());
		Assert.assertEquals(16, sub[1].getHeight());
	}

	@Test
	public void testSplitProportionally() {
		final RectangleImpl<String> a = new RectangleImpl<String>("test", 17, 97, 100, 13);
		RectangleImpl<String>[] sub = a.split(0.1);
		Assert.assertNotNull(sub);
		Assert.assertEquals(2, sub.length);
		Assert.assertEquals(17, sub[0].getX());
		Assert.assertEquals(97, sub[0].getY());
		Assert.assertEquals(10, sub[0].getWidth());
		Assert.assertEquals(13, sub[0].getHeight());
		Assert.assertEquals(17+10, sub[1].getX());
		Assert.assertEquals(97, sub[1].getY());
		Assert.assertEquals(90, sub[1].getWidth());
		Assert.assertEquals(13, sub[1].getHeight());
	}

}
