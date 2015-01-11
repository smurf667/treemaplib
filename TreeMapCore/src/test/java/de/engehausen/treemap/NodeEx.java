package de.engehausen.treemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NodeEx<T extends Number> {

	private NodeEx<T> parent;
	private List<NodeEx<T>> children;
	private T weight;
	protected final String name;

	public NodeEx(final String n, final T w) {
		name = n;
		weight = w;
	}

	public T getWeight() {
		return weight;
	}

	public NodeEx<T> getParent() {
		return parent;
	}

	public void add(final NodeEx<T> n, final NumberArithmetic<T> arithmetic) {
		if (children == null) {
			children = new ArrayList<NodeEx<T>>();
		}
		children.add(n);
		n.setParent(this, arithmetic);
	}

	public Iterator<NodeEx<T>> getChildren() {
		return children!=null?children.iterator():Collections.<NodeEx<T>>emptyList().iterator();
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

	protected void setParent(final NodeEx<T> p, final NumberArithmetic<T> arithmetic) {
		parent = p;
		NodeEx<T> runner = p;
		while (runner != null) {
			runner.weight = arithmetic.add(runner.weight, weight);
			runner = runner.getParent();
		}
	}

}
