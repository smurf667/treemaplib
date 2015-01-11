package de.engehausen.treemap.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import de.engehausen.treemap.ICancelable;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.Node;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.TreeModel;

public class SquarifiedLayoutTest extends TestCase {

	/**
	 * Tests layout with a small layout.
	 * @throws Exception in case of error.
	 */
	public void testSmallLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.SMALL;
		final SquarifiedLayout<Node> layout = new SquarifiedLayout<Node>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<Node>> rmodel = layout.layout(model, model.getRoot(), 400, 300);
		assertNotNull(rmodel);
		final List<IRectangle<Node>> list = toList(rmodel);
		assertEquals(3, list.size());
		final IRectangle<Node> root = list.get(0);
		final IRectangle<Node> one = list.get(1);
		final IRectangle<Node> two = list.get(2);
		assertEquals(one.getWidth(), two.getWidth());
		assertEquals(one.getHeight(), two.getHeight());
		final int area = 2*one.getWidth()*two.getHeight();
		assertEquals(root.getWidth()*root.getHeight(), area);
	}

	/**
	 * Tests layout when the area to layout is getting too small.
	 * @throws Exception in case of error.
	 */
	public void testTooSmall() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.DEEP_BINARY;
		final SquarifiedLayout<Node> layout = new SquarifiedLayout<Node>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<Node>> rmodel1 = layout.layout(model, model.getRoot(), 2, 2);
		final ITreeModel<IRectangle<Node>> rmodel2 = layout.layout(model, model.getRoot(), 2000, 2000);
		assertNotNull(rmodel1);
		assertNotNull(rmodel2);
		final List<IRectangle<Node>> list1 = toList(rmodel1);
		final List<IRectangle<Node>> list2 = toList(rmodel2);
		assertNotNull(list1);
		assertNotNull(list2);
		// check that rectangle building is aborted, when rectangles become too small
		assertTrue(list1.size() < list2.size());
	}

	/**
	 * Tests layout with the layout given in the <a href="http://www.win.tue.nl/~vanwijk/stm.pdf">Wijk paper</a>.
	 * @throws Exception in case of error.
	 */
	public void testWijkLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.WIJK;
		final SquarifiedLayout<Node> engine = new SquarifiedLayout<Node>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<Node>> rmodel = engine.layout(model, model.getRoot(), 600, 400);
		final List<IRectangle<Node>> list = toList(rmodel);
		assertEquals(8, list.size());
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
			final IRectangle<Node> node = list.get(i);
			assertEquals(expectedNames.get(i), node.getNode().getName());
			// record mapping node name to node position
			positions.put(node.getNode().getName(), node.getX()+","+node.getY());
			final int next = node.getWidth()*node.getHeight();
			assertTrue(last >= next);
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
			assertEquals(entry.getValue(), positions.get(entry.getKey()));
		}
	}

	/**
	 * Tests layout with <b>big</b> irregular layout.
	 * @throws Exception in case of error.
	 */
	public void testBigLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.DEEP_UNBALANCED;
		// memory stress and depth cut off test
		assertDepth(new SquarifiedLayout<Node>(Integer.MAX_VALUE).layout(model, model.getRoot(), 10000, 10000), 11);
		assertDepth(new SquarifiedLayout<Node>(1).layout(model, model.getRoot(), 1024, 768), 1);
		assertDepth(new SquarifiedLayout<Node>(2).layout(model, model.getRoot(), 1024, 768), 2);
		assertDepth(new SquarifiedLayout<Node>(6).layout(model, model.getRoot(), 1024, 768), 6);
	}

	/**
	 * Tests layout of a "flat" hierarchy.
	 * @throws Exception in case of error.
	 */
	public void testFlatLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.MANY;
		final SquarifiedLayout<Node> layout = new SquarifiedLayout<Node>(Integer.MAX_VALUE);
		final ITreeModel<IRectangle<Node>> result = layout.layout(model, model.getRoot(), 1920, 1080);
		assertDepth(result, 1);
		final Iterator<IRectangle<Node>> i = result.getChildren(result.getRoot());
		assertTrue(i.hasNext());
		// node with highest weight must come first
		assertEquals("node0", i.next().getNode().getName());
	}

	/**
	 * Tests layout with a non-root node.
	 * @throws Exception in case of error.
	 */
	public void testNonRootLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.DEEP_BINARY;
		final SquarifiedLayout<Node> engine = new SquarifiedLayout<Node>(4);
		final Node node = model.getChildren(model.getRoot()).next();
		final ITreeModel<IRectangle<Node>> rectangles = engine.layout(model, node, 512, 512);
		assertEquals((int) (Math.pow(2, 4+1)-1), toList(rectangles).size());
		assertEquals(node, rectangles.getRoot().getNode());
	}

	/**
	 * Check depth by descending the branch of the first child always.
	 * @param model the model to test
	 * @param expectedDepth the expected depth
	 * @throws Exception in case of error
	 */
	protected void assertDepth(final ITreeModel<IRectangle<Node>> model, final int expectedDepth) throws Exception {
		int depth = 0;
		IRectangle<Node> runner = model.getRoot();
		do {
			if (model.hasChildren(runner)) {
				runner = model.getChildren(runner).next();
				depth++;
			} else {
				runner = null;
			}
		} while (runner != null);
		assertEquals(expectedDepth, depth);
	}

	/**
	 * Tests layout with <b>big</b> regular layout.
	 * @throws Exception in case of error.
	 */
	public void testBigBinaryLayout() throws Exception {
		final IWeightedTreeModel<Node> model = TreeModel.DEEP_BINARY;
		final SquarifiedLayout<Node> engine = new SquarifiedLayout<Node>(4);
		final ITreeModel<IRectangle<Node>> rectangles = engine.layout(model, model.getRoot(), 512, 512);
		assertEquals((int) (Math.pow(2, 4+1)-1), toList(rectangles).size());
	}

	/**
	 * Tests <i>canceling</i> a layout computation.
	 * @throws Exception in case of error
	 */
	public void testLayoutCancelation() throws Exception {
		// run a couple of times
		for (int i = 0; i < 5; i++) {
			cancelTest();
		}
	}

	private void cancelTest() throws Exception {
		final Cancel cancel = new Cancel();
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final IWeightedTreeModel<Node> model = TreeModel.DEEP_UNBALANCED;
				cancel.setResult(new SquarifiedLayout<Node>(Integer.MAX_VALUE).layout(model, model.getRoot(), 1920, 1080, cancel));
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
		final long end = System.currentTimeMillis();
		assertNotNull(cancel.getResult());
		// thread join must be faster than 1s - okay this is a shaky test, may fail on a quantum computer
		assertTrue(end-begin < 1000L);
	}

	protected List<IRectangle<Node>> toList(final ITreeModel<IRectangle<Node>> model) {
		final List<IRectangle<Node>> list = new ArrayList<IRectangle<Node>>(16);
		final List<IRectangle<Node>> stack = new LinkedList<IRectangle<Node>>();
		stack.add(model.getRoot());
		while (!stack.isEmpty()) {
			final IRectangle<Node> node = stack.remove(0);
			list.add(node);
			if (model.hasChildren(node)) {
				for (Iterator<IRectangle<Node>> i = model.getChildren(node); i.hasNext(); ) {
					stack.add(i.next());
				}
			}
		}
		return list;
	}

	private static class Cancel implements ICancelable {

		private boolean flag = false;
		private ITreeModel<IRectangle<Node>> result;

		public void cancel() {
			flag = true;
		}

		public void setResult(final ITreeModel<IRectangle<Node>> r) {
			result = r;
		}

		public ITreeModel<IRectangle<Node>> getResult() {
			return result;
		}

		@Override
		public boolean isCanceled() {
			return flag;
		}

	}

}
