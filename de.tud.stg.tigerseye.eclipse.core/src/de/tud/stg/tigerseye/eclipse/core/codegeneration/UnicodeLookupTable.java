package de.tud.stg.tigerseye.eclipse.core.codegeneration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.TigerseyeLibraryProvider;

/**
 * {@link UnicodeLookupTable} stores the mapping between unicodes and their
 * textual representation.
 * 
 * @author Kamil Erhard
 * @author Leo Roos
 * 
 */
public class UnicodeLookupTable {
private static final Logger logger = LoggerFactory.getLogger(UnicodeLookupTable.class);


	private static volatile UnicodeLookupTable defaultUnicodeTable;
    private final Map<String, String> unicodeToName = new HashMap<String, String>();
    private final Map<String, String> nameToUnicode = new HashMap<String, String>();

    public UnicodeLookupTable(Readable... files)
	    throws FileNotFoundException {
	Scanner s;
		int cnt = 0;

	for (Readable file : files) {
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
			String characterCode = line[0];
			int characterInt = Integer.parseInt(characterCode, 16);

			/*
			 * I use a String instead of a Character to support the
			 * supplementary characters from \u100000 to \u10FFFF
			 * that exceed the 16 bits of the char primitive.
			 */
			String characterString = newString(characterInt);

						String name = line[3] != null && !line[3].isEmpty() ? line[3] : line[5] != null
								&& !line[5].isEmpty() ? line[5] : line[6];
			this.unicodeToName.put(characterString, name.trim());
			this.nameToUnicode.put(name.trim(), characterString);

					} catch (NumberFormatException ignored) {
			logger.warn("Could not decode character code. {}",
				ignored.getMessage());
					}

					cnt++;
				}
			} catch (NoSuchElementException e) {
				logger.warn("Generated log statement",e);
			}
		}
	}

    /**
     * To avoid encoding issues use the
     * {@link #UnicodeLookupTable(InputStreamReader...)} constructor.
     * 
     * @param files
     * @throws FileNotFoundException
     */
    @Deprecated
    public UnicodeLookupTable(InputStream... files)
	    throws FileNotFoundException {
	this(transformToReader(files));
    }

    private static InputStreamReader[] transformToReader(InputStream... files) {
	InputStreamReader[] reader = new InputStreamReader[files.length];
	for (int i = 0; i < files.length; i++) {
	    InputStream inputStream = files[i];
	    reader[i] = new InputStreamReader(inputStream);
	}
	return reader;
    }

    /**
     * Creates new String that contains just the given code point. Version that
     * optimizes for BMP characters.
     */
    /*
     * Suggested solution from:
     * http://java.sun.com/developer/technicalArticles/Intl/Supplementary/
     * 
     * with the note: If the method shows up in your performance measurements,
     * you may want to optimize for the very, very, very common case where the
     * code point is a BMP character:
     */
    private String newString(int codePoint) {
	if (Character.charCount(codePoint) == 1) {
	    return String.valueOf((char) codePoint);
	} else {
	    return new String(Character.toChars(codePoint));
	}
    }

	private UnicodeLookupTable() throws IOException {
		this(loadDefaultCharacterMapping());
	}

    private static InputStreamReader loadDefaultCharacterMapping()
	    throws IOException {
	InputStream openStream = TigerseyeLibraryProvider.getDefault()
		.getBundle().getEntry("resources/MathClassEx-11.txt")
		.openStream();
	return new InputStreamReader(openStream, "UTF-8");
    }

    public String unicodeCharToName(String input) {
	String output = this.unicodeToName.get(input);

	return output;
    }

    public String nameToUnicode(String input) {
	String output = this.nameToUnicode.get(input);
		return output;
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
