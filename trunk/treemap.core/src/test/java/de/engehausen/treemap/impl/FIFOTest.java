package de.engehausen.treemap.impl;

import junit.framework.Assert;

import org.junit.Test;

public class FIFOTest {

	@Test
	public void testEmpty() {
		final FIFO<String> fifo = new FIFO<String>();
		Assert.assertFalse(fifo.notEmpty());
		Assert.assertNull(fifo.pull());
	}

	@Test
	public void testPushPull() {
		final FIFO<Integer> fifo = new FIFO<Integer>();
		for (int i = 0; i < 5; i++) {
			fifo.push(Integer.valueOf(i));
		}
		Assert.assertTrue(fifo.notEmpty());
		Assert.assertEquals(Integer.valueOf(0), fifo.pull());
		for (int i = 0; i < 4; i++) {
			Assert.assertNotNull(fifo.pull());
		}
		Assert.assertFalse(fifo.notEmpty());
		Assert.assertNull(fifo.pull());
	}

}
