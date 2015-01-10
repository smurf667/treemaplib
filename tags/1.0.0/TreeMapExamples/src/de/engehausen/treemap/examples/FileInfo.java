package de.engehausen.treemap.examples;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * File information object. This object provides the name and the
 * size of a file.
 */
public class FileInfo {

	private static final String[] UNITS = {
		"b", "KB", "MB", "GB", "TB"
	};
	private static final String ZERO = "0b";
	private static NumberFormat FORMAT = new DecimalFormat("#0.000");

	private final String name;
	private final long size;

	/**
	 * Creates the file information object with the given name
	 * and size.
	 *
	 * @param aName the file name, must not be <code>null</code>.
	 * @param aSize the size of the file
	 */
	public FileInfo(final String aName, final long aSize) {
		name = aName;
		size = aSize;
	}

	/**
	 * Returns the file name.
	 * @return the file name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the file size.
	 * @return the file size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Human-readable representation of the object for
	 * debugging purposes.
	 */
	public String toString() {
		return name+"/"+size;
	}

	/**
	 * Returns the file size as a human-readable string, formatted
	 * to power of two byte units (byte, kilobyte, etc.).
	 *
	 * @return the file size as a human-readable string
	 */
	public String getSizeAsString() {
		if (size > 0) {
			final StringBuilder sb = new StringBuilder(32);
			final int idx = (int) (Math.log((double) size) / Math.log(2))/10;
			if (idx == 0) {
				sb.append(size).append(UNITS[0]);
			} else if (idx < UNITS.length) {
				sb.append(FORMAT.format(size / (double) (1<<(10*idx)))).append(UNITS[idx]);
			} else {
				sb.append("too big");
			}
			return sb.toString();
		} else {
			return ZERO;
		}
	}

}
