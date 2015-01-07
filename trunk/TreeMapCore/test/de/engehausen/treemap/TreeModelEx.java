package de.engehausen.treemap;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;

import de.engehausen.treemap.impl.DefaultArithmetics;

public class TreeModelEx implements IGenericWeightedTreeModel<NodeEx<Double>, Double> {

	public static final TreeModelEx WIJK;
	public static final TreeModelEx SMALL;
	public static final TreeModelEx DEFAULT;
	public static final TreeModelEx ROOT;
	public static final TreeModelEx TWOLEVEL;
	public static final TreeModelEx DEEP_BINARY;
	/** ~half a million nodes... 11 levels deep */
	public static final TreeModelEx DEEP_UNBALANCED;
	/** many nodes on the same level */
	public static final TreeModelEx MANY;

	static {
		final NumberArithmetic<Double> arithmetic = DefaultArithmetics.doubles();
		final NodeEx<Double> r = new NodeEx<Double>("root", Double.valueOf(0));
		DEFAULT = new TreeModelEx(r);
		final NodeEx<Double> a = new NodeEx<Double>("A", Double.valueOf(4));
		final NodeEx<Double> b = new NodeEx<Double>("B", Double.valueOf(1));
		final NodeEx<Double> c = new NodeEx<Double>("C", Double.valueOf(13));
		final NodeEx<Double> d = new NodeEx<Double>("D", Double.valueOf(4));
		final NodeEx<Double> e = new NodeEx<Double>("E", Double.valueOf(15));
		final NodeEx<Double> f = new NodeEx<Double>("F", Double.valueOf(3));
		final NodeEx<Double> g = new NodeEx<Double>("G", Double.valueOf(1));
		final NodeEx<Double> h = new NodeEx<Double>("H", Double.valueOf(6));
		final NodeEx<Double> i = new NodeEx<Double>("I", Double.valueOf(1));
		r.add(a, arithmetic);
		r.add(b, arithmetic);
		r.add(c, arithmetic);
		r.add(d, arithmetic);
		b.add(e, arithmetic);
		b.add(f, arithmetic);
		c.add(g, arithmetic);
		g.add(h, arithmetic);
		g.add(i, arithmetic);
		final NodeEx<Double> s1 = new NodeEx<Double>("root", Double.valueOf(0));
		SMALL = new TreeModelEx(s1);
		s1.add(new NodeEx<Double>("small1", Double.valueOf(5000)), arithmetic);
		s1.add(new NodeEx<Double>("small2", Double.valueOf(5000)), arithmetic);
		final NodeEx<Double> w1 = new NodeEx<Double>("root", Double.valueOf(0));
		WIJK = new TreeModelEx(w1);
		w1.add(new NodeEx<Double>("e2", Double.valueOf(2)), arithmetic);
		w1.add(new NodeEx<Double>("g1", Double.valueOf(1)), arithmetic);
		w1.add(new NodeEx<Double>("a6", Double.valueOf(6)), arithmetic);
		w1.add(new NodeEx<Double>("c4", Double.valueOf(4)), arithmetic);
		w1.add(new NodeEx<Double>("d3", Double.valueOf(3)), arithmetic);
		w1.add(new NodeEx<Double>("b6", Double.valueOf(6)), arithmetic);
		w1.add(new NodeEx<Double>("f2", Double.valueOf(2)), arithmetic);
		ROOT = new TreeModelEx(new NodeEx<Double>("root", Double.valueOf(1)));
		final NodeEx<Double> x = new NodeEx<Double>("root", Double.valueOf(2));
		TWOLEVEL = new TreeModelEx(x);
		x.add(new NodeEx<Double>("child", Double.valueOf(5)), arithmetic);

		DEEP_BINARY = new TreeModelEx(new NodeEx<Double>("root", Double.valueOf(0)));
		addNodes(DEEP_BINARY.getRoot(), 0, 6);
		DEEP_UNBALANCED = new TreeModelEx(new NodeEx<Double>("root", Double.valueOf(0)));
		addNodes(DEEP_UNBALANCED.getRoot(), "n", 0, 10, new Random(2010));

		final NodeEx<Double> manyRoot = new NodeEx<Double>("root", Double.valueOf(0));
		MANY = new TreeModelEx(manyRoot);
		for (int j = 0; j < 300; j++) {
			manyRoot.add(new NodeEx<Double>("node"+j, Double.valueOf(300-j)), arithmetic);
		}
	}

	private static void addNodes(final NodeEx<Double> n, final int depth, final int max) {
		final NodeEx<Double> n1 = new NodeEx<Double>("A"+depth, Double.valueOf(1));
		final NodeEx<Double> n2 = new NodeEx<Double>("B"+depth, Double.valueOf(1));
		n.add(n1, DefaultArithmetics.doubles());
		n.add(n2, DefaultArithmetics.doubles());
		if (depth < max) {
			addNodes(n1, depth+1, max);
			addNodes(n2, depth+1, max);
		}
	}

	private static void addNodes(final NodeEx<Double> n, final String prefix, final int depth, final int max, final Random rnd) {
		final StringBuilder sb = new StringBuilder(25);
		sb.append(prefix).append(".");
		final int cut = sb.length();
		for (int i = rnd.nextInt(5); i >= 0; i--) {
			sb.setLength(cut);
			sb.append(i);
			final String name = sb.toString();
			final NodeEx<Double> c = new NodeEx<Double>(name, Double.valueOf(rnd.nextDouble()));
			n.add(c, DefaultArithmetics.doubles());
			if (depth < max) {
				addNodes(c, name, depth+1, max, rnd);
			}
		}
	}

	private final NodeEx<Double> root;

	public TreeModelEx(final NodeEx<Double> r) {
		root = r;
	}

	@Override
	public Double getWeight(NodeEx<Double> node) {
		return node.getWeight();
	}

	@Override
	public Iterator<NodeEx<Double>> getChildren(NodeEx<Double> node) {
		return node.getChildren();
	}

	@Override
	public NodeEx<Double> getParent(NodeEx<Double> node) {
		return node.getParent();
	}

	@Override
	public NodeEx<Double> getRoot() {
		return root;
	}

	@Override
	public boolean hasChildren(NodeEx<Double> node) {
		return node.hasChildren();
	}

	public void dump(final PrintStream ps) {
		dump(getRoot(), 0, ps);
	}

	protected void dump(final NodeEx<Double> n, final int depth, final PrintStream ps) {
		for (int i = 0; i < depth; i++) {
			ps.print(" ");
		}
		ps.println(n.toString());
		for (Iterator<NodeEx<Double>> i = n.getChildren(); i.hasNext(); ) {
			dump(i.next(), depth+1, ps);
		}
	}

	@Override
	public NumberArithmetic<Double> getArithmetic() {
		return DefaultArithmetics.doubles();
	}

}
