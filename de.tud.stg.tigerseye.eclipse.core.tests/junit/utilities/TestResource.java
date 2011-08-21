package utilities;

import java.io.File;

/**
 * A wrapper around test resources for easier handling and central definition of such as well as common accessor methods for them.
 * 
 * @author Leo Roos
 *
 */
public class TestResource {

	private static final File resourcesRoot = new File("resources");
	
	protected final String path;
	
	/**
	 * @param path the resources folder relative path.
	 */
	protected TestResource(String path) {
		this.path = path;
	}
	
	public File getFile() {
		File file = new File(resourcesRoot, path);
		if(!file.exists())
			throw new IllegalArgumentException("Declared resource does not exist: " + file);
		return file;
	}
	
}
