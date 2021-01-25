package de.engehausen.treemap.examples;

import java.io.File;

import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.impl.GenericTreeModel;

/**
 * A weighted tree model of files and directories. The model can be built
 * from a given starting point and will recursive into all sub-directories.
 */
public class FileModel extends GenericTreeModel<FileInfo> {

	/**
	 * Creates a file model starting at the given "root" directory.
	 * @param rootDir the starting directory for building the model,
	 * must not be {@code null} and must be a valid directory.
	 * @return a file model starting at the given "root" directory.
	 * @throws IllegalArgumentException if the root directory is not valid.
	 */
	public static IWeightedTreeModel<FileInfo> createFileModel(final String rootDir) {
		final File dir = new File(rootDir);
		if (dir.isDirectory()) {
			final FileModel result = new FileModel();
			traverse(null, dir, result);
			return result;
		} else {
			throw new IllegalArgumentException(rootDir+" not a directory");
		}
	}

	/**
	 * Recursively traverses directories.
	 *
	 * @param parent the parent file information
	 * @param f the current file or directory
	 * @param model the current file model being built
	 */
    private static void traverse(final FileInfo parent, final File f, final FileModel model) {
		if (f.isDirectory()) {
			final String node = f.getAbsolutePath();
			final FileInfo info = new FileInfo(node, 0);
			model.add(info, 0, parent);
			final File[] files = f.listFiles();
			if (files != null) {
				for (int i = files.length - 1; i >= 0; i--) {
					traverse(info, files[i], model);
				}
			}
		} else {
			final long size = f.length();
			final FileInfo info = new FileInfo(f.getAbsolutePath(), size);
			model.add(info, size, parent);
		}
	}

	protected FileModel() {
		super();
	}

}
