package de.engehausen.treemap.impl;

import org.junit.Assert;
import org.junit.Test;

public class BuildControlTest {

	@Test
	public void testBehavior() {
		final BuildControl control = new BuildControl();
		Assert.assertFalse(control.isCanceled());
		control.cancel();
		Assert.assertTrue(control.isCanceled());
	}
	
}
