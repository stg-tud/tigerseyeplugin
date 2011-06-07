package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

enum Resources {
	defaultbundleclasspath("projectwithdefaultbundleclasspath"), //
	somebundleclasspath("projectwithspecialbundleclasspath"), //
	unknownformatfile("filewithunknownformat.undf"), //
	jarfile("jartestbundle.jar"), //
	developmentProject("developmentprojectwithmetadata"), //
	;

	public final String NAME;

	private Resources(String name) {
		this.NAME = name;
	}

	public File getFile() {
		URI resource = getURI();
		return new File(resource);
	}

	public URI getURI()  {
		URI resource;
		try {
			URL url = DSLClasspathResolverTest.class.getResource(
					"resources/" + NAME);
			if(url == null)
				System.out.println("Resources.getURI() resource " + NAME +" not found.");
			resource = url.toURI();
		} catch (URISyntaxException e) {
			System.out.println("Resources.getURI()" + e);
			return null;
		}
		return resource;
	}
}