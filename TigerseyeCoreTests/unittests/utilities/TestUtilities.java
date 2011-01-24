package utilities;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestUtilities {

    public static void initLogger(){
//	BasicConfigurator.configure();
	Logger.getRootLogger().setLevel(Level.ALL);
    }
    
}
