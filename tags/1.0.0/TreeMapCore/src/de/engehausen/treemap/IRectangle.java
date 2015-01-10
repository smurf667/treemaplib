package de.engehausen.treemap;

/**
 * Represents the rectangle in a tree map.
 * @param <N> the type of node the rectangle supports.
 */
public interface IRectangle<N> {

	/**
	 * Returns the horizontal component of the starting point of the rectangle.
	 * @return the horizontal component of the starting point of the rectangle.
	 */
	int getX();

	/**
	 * Returns the vertical component of the starting point of the rectangle.
	 * @return the vertical component of the starting point of the rectangle.
	 */
	int getY();

	/**
	 * Returns the width of the rectangle.
	 * @return the width of the rectangle.
	 */
	int getWidth();

	/**
	 * Returns the height of the rectangle.
	 * @return the height of the rectangle.
	 */
	int getHeight();

	/**
	 * Checks if the given coordinate lies inside of the area
	 * of the rectangle.
	 * @param x the horizontal component of the point to check.
	 * @param y the vertical component of the point to check.
	 * @return <code>true</code> if the pointed is contained in
	 * the rectangle, <code>false</code> otherwise.
	 */
	boolean contains(int x, int y);

	/**
	 * Returns the node belonging to this rectangle.
	 * @return the node belonging to this rectangle.
	 */
	N getNode();

}
