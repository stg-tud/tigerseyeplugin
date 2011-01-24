package de.tud.stg.popart.builder.eclipse.resources.src;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class JavaOrigin {
    
    private static final Logger logger = Logger.getLogger(JavaOrigin.class);
    
    public static void main(String[] args) throws IOException, Exception {
	logger.info("Hello World");
	URL resource = JavaOrigin.class.getResource(".");
	File file = new File(new File(resource.toURI()), "touched");
	System.out.println("touching: " + file);
	FileUtils.touch(file);
	boolean exists = file.exists();
	System.out.println(exists);
    }

}
