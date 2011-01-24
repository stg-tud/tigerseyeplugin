package de.lroos;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	static {
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
	}

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			logger.error("need at least one argument");
			return;
		}

		String folderName = args[0];
		File rootFolder = new File(folderName);
		Sfl4jtransformer t;
		if (args.length < 2) {
			t = new Sfl4jtransformer(rootFolder);
		} else {
			String output = args[1];
			t = new Sfl4jtransformer(rootFolder, new File(output));
		}

		try {
			t.transform();
		} catch (IOException e) {
			logger.error(
					"Transformation could not be accomplished. Processed directory is possibly in an undefined state",
					e);
		}

	}

}
