package de.engehausen.treemap.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.treemap.ICancelable;
import de.engehausen.treemap.IGenericWeightedTreeModel;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.NodeEx;
import de.engehausen.treemap.TreeModelEx;

public class GenericSquarifiedLayoutTest {

	/**
	 * Tests layout with a small layout.
	 */
	@Test
	public void testSmallLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.SMALL;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> layout = new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<NodeEx<Double>>> rmodel = layout.layout(model, model.getRoot(), 400, 300);
		Assert.assertNotNull(rmodel);
		final List<IRectangle<NodeEx<Double>>> list = toList(rmodel);
		Assert.assertEquals(3, list.size());
		final IRectangle<NodeEx<Double>> root = list.get(0);
		final IRectangle<NodeEx<Double>> one = list.get(1);
		final IRectangle<NodeEx<Double>> two = list.get(2);
		Assert.assertEquals(one.getWidth(), two.getWidth());
		Assert.assertEquals(one.getHeight(), two.getHeight());
		final int area = 2*one.getWidth()*two.getHeight();
		Assert.assertEquals(root.getWidth()*root.getHeight(), area);
	}

	/**
	 * Tests layout when the area to layout is getting too small.
	 */
	@Test
	public void testTooSmall() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.DEEP_BINARY;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> layout = new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<NodeEx<Double>>> rmodel1 = layout.layout(model, model.getRoot(), 2, 2);
		final ITreeModel<IRectangle<NodeEx<Double>>> rmodel2 = layout.layout(model, model.getRoot(), 2000, 2000);
		Assert.assertNotNull(rmodel1);
		Assert.assertNotNull(rmodel2);
		final List<IRectangle<NodeEx<Double>>> list1 = toList(rmodel1);
		final List<IRectangle<NodeEx<Double>>> list2 = toList(rmodel2);
		Assert.assertNotNull(list1);
		Assert.assertNotNull(list2);
		// check that rectangle building is aborted, when rectangles become too small
		Assert.assertTrue(list1.size() < list2.size());
	}

	/**
	 * Tests layout with the layout given in the <a href="http://www.win.tue.nl/~vanwijk/stm.pdf">Wijk paper</a>.
	 */
	@Test
	public void testWijkLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.WIJK;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> engine = new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<NodeEx<Double>>> rmodel = engine.layout(model, model.getRoot(), 600, 400);
		final List<IRectangle<NodeEx<Double>>> list = toList(rmodel);
		Assert.assertEquals(8, list.size());
		int last = Integer.MAX_VALUE;
		final Map<String, String> positions = new HashMap<String, String>();
		final List<String> expectedNames = new ArrayList<String>(8);
		expectedNames.add("root");
		expectedNames.add("a6");
		expectedNames.add("b6");
		expectedNames.add("c4");
		expectedNames.add("d3");
		expectedNames.add("e2");
		expectedNames.add("f2");
		expectedNames.add("g1");
		// verify that areas are not getting bigger with each entry
		for (int i = 0; i < 8; i++) {
			final IRectangle<NodeEx<Double>> node = list.get(i);
			Assert.assertEquals(expectedNames.get(i), node.getNode().getName());
			// record mapping node name to node position
			positions.put(node.getNode().getName(), node.getX()+","+node.getY());
			final int next = node.getWidth()*node.getHeight();
			Assert.assertTrue(last >= next);
			last = next;
		}
		final Map<String, String> expected = new HashMap<String, String>();
		expected.put("a6", "0,0");
		expected.put("b6", "0,200");
		expected.put("c4", "300,0");
		expected.put("d3", "471,0");
		expected.put("e2", "300,233");
		expected.put("f2", "420,233");
		expected.put("g1", "540,233");
		for (Map.Entry<String, String> entry : expected.entrySet()) {
			Assert.assertEquals(entry.getValue(), positions.get(entry.getKey()));
		}
	}

	/**
	 * Tests layout with <b>big</b> irregular layout.
	 */
	@Test
	public void testBigLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.DEEP_UNBALANCED;
		// memory stress and depth cut off test
		assertDepth(new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE).layout(model, model.getRoot(), 10000, 10000), 11);
		assertDepth(new GenericSquarifiedLayout<NodeEx<Double>, Double>(1).layout(model, model.getRoot(), 1024, 768), 1);
		assertDepth(new GenericSquarifiedLayout<NodeEx<Double>, Double>(2).layout(model, model.getRoot(), 1024, 768), 2);
		assertDepth(new GenericSquarifiedLayout<NodeEx<Double>, Double>(6).layout(model, model.getRoot(), 1024, 768), 6);
	}

	/**
	 * Tests layout of a "flat" hierarchy.
	 */
	@Test
	public void testFlatLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.MANY;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> layout = new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<NodeEx<Double>>> result = layout.layout(model, model.getRoot(), 1920, 1080);
		assertDepth(result, 1);
		final Iterator<IRectangle<NodeEx<Double>>> i = result.getChildren(result.getRoot());
		Assert.assertTrue(i.hasNext());
		// node with highest weight must come first
		Assert.assertEquals("node0", i.next().getNode().getName());
	}

	/**
	 * Tests layout with a non-root node.
	 */
	@Test
	public void testNonRootLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.DEEP_BINARY;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> engine = new GenericSquarifiedLayout<NodeEx<Double>, Double>(4);
		final NodeEx<Double> node = model.getChildren(model.getRoot()).next();
		final ITreeModel<IRectangle<NodeEx<Double>>> rectangles = engine.layout(model, node, 512, 512);
		Assert.assertEquals((int) (Math.pow(2, 4+1)-1), toList(rectangles).size());
		Assert.assertEquals(node, rectangles.getRoot().getNode());
	}

	/**
	 * Tests layout with <b>big</b> regular layout.
	 */
	@Test
	public void testBigBinaryLayout() {
		final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.DEEP_BINARY;
		final GenericSquarifiedLayout<NodeEx<Double>, Double> engine = new GenericSquarifiedLayout<NodeEx<Double>, Double>(4);
		final ITreeModel<IRectangle<NodeEx<Double>>> rectangles = engine.layout(model, model.getRoot(), 512, 512);
		Assert.assertEquals((int) (Math.pow(2, 4+1)-1), toList(rectangles).size());
	}

	/**
	 * Tests <i>canceling</i> a layout computation.
	 * @throws InterruptedException in case of error
	 */
	@Test
	public void testLayoutCancelation() throws InterruptedException {
		// run a couple of times
		for (int i = 0; i < 5; i++) {
			cancelTest();
		}
	}

	/**
	 * Check depth by descending the branch of the first child always.
	 * @param model the model to test
	 * @param expectedDepth the expected depth
	 */
	protected void assertDepth(final ITreeModel<IRectangle<NodeEx<Double>>> model, final int expectedDepth) {
		int depth = 0;
		IRectangle<NodeEx<Double>> runner = model.getRoot();
		do {
			if (model.hasChildren(runner)) {
				runner = model.getChildren(runner).next();
				depth++;
			} else {
				runner = null;
			}
		} while (runner != null);
		Assert.assertEquals(expectedDepth, depth);
	}

	private void cancelTest() throws InterruptedException {
		final Cancel cancel = new Cancel();
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final IGenericWeightedTreeModel<NodeEx<Double>, Double> model = TreeModelEx.DEEP_UNBALANCED;
				cancel.setResult(new GenericSquarifiedLayout<NodeEx<Double>, Double>(Integer.MAX_VALUE).layout(model, model.getRoot(), 1920, 1080, cancel));
			}
		});
		t.start();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// ignore
		}
		cancel.cancel();
		final long begin = System.currentTimeMillis();
		t.join();
		final long end = System.currentTimeMillis(); // TODO NANO
		Assert.assertNotNull(cancel.getResult());
		// thread join must be faster than 1s - okay this is a shaky test, may fail on a quantum computer
		Assert.assertTrue(end-begin < 1000L);
	}

	protected List<IRectangle<NodeEx<Double>>> toList(final ITreeModel<IRectangle<NodeEx<Double>>> model) {
		final List<IRectangle<NodeEx<Double>>> list = new ArrayList<IRectangle<NodeEx<Double>>>(16);
		final List<IRectangle<NodeEx<Double>>> stack = new LinkedList<IRectangle<NodeEx<Double>>>();
		stack.add(model.getRoot());
		while (!stack.isEmpty()) {
			final IRectangle<NodeEx<Double>> node = stack.remove(0);
			list.add(node);
			if (model.hasChildren(node)) {
				for (Iterator<IRectangle<NodeEx<Double>>> i = model.getChildren(node); i.hasNext(); ) {
					stack.add(i.next());
				}
			}
		}
		return list;
	}

	private static class Cancel implements ICancelable {

		private boolean flag = false;
		private ITreeModel<IRectangle<NodeEx<Double>>> result;

		public void cancel() {
			flag = true;
		}

		public void setResult(final ITreeModel<IRectangle<NodeEx<Double>>> r) {
			result = r;
		}

		public ITreeModel<IRectangle<NodeEx<Double>>> getResult() {
			return result;
		}

		@Override
		public boolean isCanceled() {
			return flag;
		}

	}

}
