package utilities;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestUtilities {

    public static void initLogger(){
//	BasicConfigurator.configure();
	Logger.getRootLogger().setLevel(Level.ALL);
    }

	public static File[] getFilesRelativeToRoot(File resFile, String ... expected) {
		File[] expectedFiles = new File[expected.length];
		for (int i = 0 ; i < expected.length; i ++) {
			expectedFiles[i] = new File(resFile, expected[i]);
		}
		return expectedFiles;
	}
    
}
