package de.engehausen.treemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Node {

	private Node parent;
	private List<Node> children;
	private long weight;
	protected final String name;

	public Node(final String n, final long w) {
		name = n;
		weight = w;
	}

	public Node getParent() {
		return parent;
	}

	public void add(final Node n) {
		if (children == null) {
			children = new ArrayList<Node>();
		}
		children.add(n);
		n.setParent(this);
	}

	public Iterator<Node> getChildren() {
		return children!=null?children.iterator():Collections.<Node>emptyList().iterator();
	}

	public boolean hasChildren() {
		return children!=null?children.size()>0:false;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	protected void setParent(final Node p) {
		parent = p;
		Node runner = p;
		while (runner != null) {
			runner.addWeight(weight);
			runner = runner.getParent();
		}
	}

	protected long getWeight() {
		return weight;
	}

	protected void addWeight(final long w) {
		weight += w;
	}

}
