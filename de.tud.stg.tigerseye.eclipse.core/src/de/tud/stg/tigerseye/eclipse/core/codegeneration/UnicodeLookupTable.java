package de.tud.stg.tigerseye.eclipse.core.codegeneration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.TigerseyeLibraryProvider;

/**
 * {@link UnicodeLookupTable} stores the mapping between unicodes and their textual representation.
 * 
 * @author Kamil Erhard
 * 
 */
public class UnicodeLookupTable {
private static final Logger logger = LoggerFactory.getLogger(UnicodeLookupTable.class);


	private static volatile UnicodeLookupTable defaultUnicodeTable;
	private final Map<Character, String> unicodeToName = new HashMap<Character, String>();
	private final Map<String, Character> nameToUnicode = new HashMap<String, Character>();

	public UnicodeLookupTable(InputStream... files) throws FileNotFoundException {
		Scanner s;
		int cnt = 0;

		for (InputStream file : files) {
			try {
				s = new Scanner(file);

				s.useDelimiter("\n");

				while (s.hasNextLine()) {

		    /*
		     * TODO it seems that s.next.split(";") should return a 7
		     * element array. Instead of an assertion after the split
		     * call line is initialized with an seven element array
		     * which is overwritten with the following assignment.
		     */
		    // String[] line = new String[7];

					String[] line = s.next().split(";");

					try {
						int i = Integer.decode("0x" + line[0]);
						char c = (char) i;

						String name = line[3] != null && !line[3].isEmpty() ? line[3] : line[5] != null
								&& !line[5].isEmpty() ? line[5] : line[6];
						this.unicodeToName.put(c, name.trim());
						this.nameToUnicode.put(name.trim(), c);

					} catch (NumberFormatException ignored) {
					}

					cnt++;
				}
			} catch (NoSuchElementException e) {
				logger.info(String.valueOf(cnt));
				// logger.info(s.);
				logger.warn("Generated log statement",e);
			}
		}
	}

	private UnicodeLookupTable() throws IOException {
		this(TigerseyeLibraryProvider.getDefault().getBundle().getEntry("resources/MathClassEx-11.txt").openStream());
	}

	public String transform(char input) {
		String output = this.unicodeToName.get(input);

		return output;
	}

	public Character transform(String input) {
		Character output = this.nameToUnicode.get(input);

		return output;
	}

	public static void main(String[] args) {
		UnicodeLookupTable uclt = null;
		try {
			uclt = new UnicodeLookupTable(new FileInputStream("MathClassEx-11.txt"));
	    logger.info(uclt.transform('\"'));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.warn("Generated log statement",e);
		}

	}

	public static UnicodeLookupTable getDefaultInstance() {
		try {
			if (defaultUnicodeTable == null) {
				synchronized (UnicodeLookupTable.class) {
					if (defaultUnicodeTable == null) {
						defaultUnicodeTable = new UnicodeLookupTable();
					}
				}
			}

			return defaultUnicodeTable;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static synchronized void setDefaultInstance(UnicodeLookupTable table) {
		defaultUnicodeTable = table;
	}
}
