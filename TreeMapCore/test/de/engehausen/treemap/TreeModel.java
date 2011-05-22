package de.engehausen.treemap;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;

public class TreeModel implements IWeightedTreeModel<Node> {
	
	public static final TreeModel WIJK;
	public static final TreeModel SMALL;
	public static final TreeModel DEFAULT;
	public static final TreeModel ROOT;
	public static final TreeModel TWOLEVEL;
	public static final TreeModel DEEP_BINARY;
	/** ~half a million nodes... 11 levels deep */
	public static final TreeModel DEEP_UNBALANCED;
	/** many nodes on the same level */
	public static final TreeModel MANY;
	
	static {
		final Node r = new Node("root", 0);
		DEFAULT = new TreeModel(r);
		final Node a = new Node("A", 4);
		final Node b = new Node("B", 1);
		final Node c = new Node("C", 13);
		final Node d = new Node("D", 4);
		final Node e = new Node("E", 15);
		final Node f = new Node("F", 3);
		final Node g = new Node("G", 1);
		final Node h = new Node("H", 6);
		final Node i = new Node("I", 1);
		r.add(a);
		r.add(b);
		r.add(c);
		r.add(d);
		b.add(e);
		b.add(f);
		c.add(g);
		g.add(h);
		g.add(i);
		final Node s1 = new Node("root", 0);
		SMALL = new TreeModel(s1);
		s1.add(new Node("small1", 5000));
		s1.add(new Node("small2", 5000));
		final Node w1 = new Node("root", 0);
		WIJK = new TreeModel(w1);
		w1.add(new Node("a6", 6));
		w1.add(new Node("b6", 6));
		w1.add(new Node("c4", 4));
		w1.add(new Node("d3", 3));
		w1.add(new Node("e2", 2));
		w1.add(new Node("f2", 2));
		w1.add(new Node("g1", 1));
		ROOT = new TreeModel(new Node("root", 1));
		final Node x = new Node("root", 2);
		TWOLEVEL = new TreeModel(x);
		x.add(new Node("child", 5));
		
		DEEP_BINARY = new TreeModel(new Node("root", 0));
		addNodes(DEEP_BINARY.getRoot(), 0, 6);
		DEEP_UNBALANCED = new TreeModel(new Node("root", 0));
		addNodes(DEEP_UNBALANCED.getRoot(), "n", 0, 10, new Random(2010));
		
		final Node manyRoot = new Node("root", 0);
		MANY = new TreeModel(manyRoot);
		for (int j = 0; j < 300; j++) {
			manyRoot.add(new Node("node"+j, 300-j));
		}		
	}
	
	private static void addNodes(final Node n, final int depth, final int max) {
		final Node n1 = new Node("A"+depth, 1);
		final Node n2 = new Node("B"+depth, 1);
		n.add(n1);
		n.add(n2);
		if (depth < max) {
			addNodes(n1, depth+1, max);
			addNodes(n2, depth+1, max);		  
		}
	}
	
	private static void addNodes(final Node n, final String prefix, final int depth, final int max, final Random rnd) {
		final StringBuilder sb = new StringBuilder(25);
		sb.append(prefix).append(".");
		final int cut = sb.length();
		for (int i = rnd.nextInt(5); i >= 0; i--) {
			sb.setLength(cut);
			sb.append(i);
			final String name = sb.toString();
			final Node c = new Node(name, rnd.nextInt(8192));
			n.add(c);
			if (depth < max) {
				addNodes(c, name, depth+1, max, rnd);
			}
		}
	}
	
	private final Node root;
	
	public TreeModel(final Node r) {
		root = r;
	}

	@Override
	public long getWeight(Node node) {
		return node.getWeight();
	}

	@Override
	public Iterator<Node> getChildren(Node node) {
		return node.getChildren();
	}

	@Override
	public Node getParent(Node node) {
		return node.getParent();
	}

	@Override
	public Node getRoot() {
		return root;
	}

	@Override
	public boolean hasChildren(Node node) {
		return node.hasChildren();
	}
	
	public void dump(final PrintStream ps) {
		dump(getRoot(), 0, ps);
	}
	
	protected void dump(final Node n, final int depth, final PrintStream ps) {
		for (int i = 0; i < depth; i++) {
			ps.print(" ");
		}
		ps.println(n.toString());
		for (Iterator<Node> i = n.getChildren(); i.hasNext(); ) {
			dump(i.next(), depth+1, ps);
		}
	}

}
