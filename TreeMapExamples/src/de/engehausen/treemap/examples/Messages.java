package de.engehausen.treemap.examples;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message helper to display UI information in various languages.
 */
public class Messages {

	private static final String BUNDLE_NAME = "de.engehausen.treemap.examples.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Returns a string for the given key in the currently active language
	 * or an appropriate fall back string.
	 * @param key the key for the value to look up, must not be <code>null</code>.
	 * @return the language string, or "!<key>!" if none can be found
	 */
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Checks if the given key exists in the underlying bundles object.
	 * @param key the key to check; must not be <code>null</code>
	 * @return <code>true</code> if the key exists, <code>false</code> otherwise.
	 */
	public static boolean contains(final String key) {
		return RESOURCE_BUNDLE.containsKey(key);
	}
}
