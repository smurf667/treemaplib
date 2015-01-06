package de.engehausen.treemap.impl;

import junit.framework.TestCase;

public class RectangleImplTest extends TestCase {
	
	public void testSimple() throws Exception {
		final RectangleImpl<String> rect = new RectangleImpl<String>(null, 4, 8, 16, 32);
		assertEquals(4, rect.getX());
		assertEquals(8, rect.getY());
		assertEquals(16, rect.getWidth());
		assertEquals(32, rect.getHeight());
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
			assertEquals(expected[i], rect.contains(coords[i][0], coords[i][1]));
		}
	}
	
	public void testCompare() throws Exception {
		final RectangleImpl<String> a = new RectangleImpl<String>("a", 4, 8, 16, 32);
		final RectangleImpl<String> b = new RectangleImpl<String>("a", 4, 8, 16, 32);
		final RectangleImpl<String> c = new RectangleImpl<String>("c", 4, 8, 32, 16);
		assertEquals(a, b);
		assertEquals(b, a);
		assertFalse(a.equals(c));
		assertFalse(c.equals(a));		
	}
	
	public void testSplitHorizontally() throws Exception {
		final RectangleImpl<String> a = new RectangleImpl<String>("a", 0, 0, 16, 32);
		RectangleImpl<String>[] sub = a.split(0.5);
		assertNotNull(sub);
		assertEquals(2, sub.length);
		assertEquals(0, sub[0].getX());
		assertEquals(0, sub[0].getY());
		assertEquals(16, sub[0].getWidth());
		assertEquals(16, sub[0].getHeight());
		assertEquals(0, sub[1].getX());
		assertEquals(16, sub[1].getY());
		assertEquals(16, sub[1].getWidth());
		assertEquals(16, sub[1].getHeight());
	}

	public void testSplitVertically() throws Exception {
		final RectangleImpl<String> a = new RectangleImpl<String>("hello world", 0, 0, 32, 16);
		RectangleImpl<String>[] sub = a.split(0.5);
		assertNotNull(sub);
		assertEquals(2, sub.length);
		assertEquals(0, sub[0].getX());
		assertEquals(0, sub[0].getY());
		assertEquals(16, sub[0].getWidth());
		assertEquals(16, sub[0].getHeight());
		assertEquals(16, sub[1].getX());
		assertEquals(0, sub[1].getY());
		assertEquals(16, sub[1].getWidth());
		assertEquals(16, sub[1].getHeight());
	}

	public void testSplitProportionally() throws Exception {
		final RectangleImpl<String> a = new RectangleImpl<String>("test", 17, 97, 100, 13);
		RectangleImpl<String>[] sub = a.split(0.1);
		assertNotNull(sub);
		assertEquals(2, sub.length);
		assertEquals(17, sub[0].getX());
		assertEquals(97, sub[0].getY());
		assertEquals(10, sub[0].getWidth());
		assertEquals(13, sub[0].getHeight());
		assertEquals(17+10, sub[1].getX());
		assertEquals(97, sub[1].getY());
		assertEquals(90, sub[1].getWidth());
		assertEquals(13, sub[1].getHeight());
	}

}
