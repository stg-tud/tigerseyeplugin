package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link UnicodeLookupTable} stores the mapping between unicodes and their
 * textual representation. <br>
 * Use the {@link #load(Readable...)} method to initialize the
 * {@link UnicodeLookupTable} object.
 * 
 * @author Kamil Erhard
 * @author Leo Roos
 * 
 */
public class UnicodeLookupTable {
    private static final Logger logger = LoggerFactory
	    .getLogger(UnicodeLookupTable.class);

    static final String UNDEFINEDNAME = "NO_REPRESENTATION_AVAILABLE";

    private final Map<String, String> unicodeToName = new HashMap<String, String>();
    private final Map<String, String> nameToUnicode = new HashMap<String, String>();

    /**
     * The {@link #load(Readable...)} method returns the instance of the current
     * object this can be used to initialize the {@link UnicodeLookupTable} on a
     * single line:
     * 
     * <pre>
     * UnicodeLookupTable ult = new UnicodeLookupTable().load(reader);
     * </pre>
     */
    public UnicodeLookupTable() {

    }

    /**
     * Load the mappings defined in passed parameters. The expected format is
     * described on
     * 
     * <pre>
     *            http://unicode.org/Public/math/revision-11/MathClassEx-11.txt
     * </pre>
     * 
     * @param files
     *            containing the mappings
     * @return a instance of itself. Can be used for method chained
     *         initialization
     */
    public UnicodeLookupTable load(Reader... files) {
	for (Reader file : files) {
	    addUnicodesOfFile(file);
	}
	return this;
    }

    private void addUnicodesOfFile(Reader file) {
	Scanner s = new Scanner(file);
	try {
	    while (s.hasNextLine()) {
		String nextLine = s.nextLine();
		if (!nextLine.trim().isEmpty() && !nextLine.startsWith("#")) {
		    processNextLine(nextLine);

		} else
		    ;// ignore line beginning with #.
	    }
	} finally {
	    s.close();
	}
    }

    /**
     * @param codePoint
     *            a String representation of a hexadecimal number, e.g. F243
     * @return the parsed codePoint or {@code -1} if the the passed string has
     *         an unsupported format
     */
    int hasValidCharacterCodeForm(String codePoint) {
	try {
	    int parseInt = Integer.parseInt(codePoint, 16);
	    if (Character.isDefined(parseInt))
		return parseInt;
	    else {
		return -1;
	    }
	} catch (NumberFormatException e) {
	    return -1;
	}
    }

    private void processNextLine(String nextLine) {
	    UnicodeNamePair uniname = addNewUnicodeCharacter(nextLine);
	    if (uniname == null) {
		logger.debug("Following line has an unsupported format: {}",
			nextLine);
		return;
	    }
	    this.unicodeToName.put(uniname.characterString, uniname.name);
	    this.nameToUnicode.put(uniname.name, uniname.characterString);
    }

    /*
     * Returns a new {@link UnicodeNamePair} or null if the format was invalid
     */
    @CheckForNull
    UnicodeNamePair addNewUnicodeCharacter(String line) {
	String[] nextLine = line.split(";");

	String characterCode = getChecked(nextLine, 0);

	int characterInt = hasValidCharacterCodeForm(characterCode);
	if (characterInt < 0)
	    return null;

	String characterString = newString(characterInt);

	String unicodeChar = getChecked(nextLine, 3);
	String descrComment = getChecked(nextLine, 5);
	String unicodeName = getChecked(nextLine, 6);

	String name;

	if (unicodeChar != null && !unicodeChar.isEmpty()) {
	    name = unicodeChar;
	} else if (descrComment != null && !descrComment.isEmpty()) {
	    name = descrComment;
	} else if (unicodeName != null && !unicodeName.isEmpty()) {
	    name = unicodeName;
	} else {
	    // There should be no such case, if there is, it should still be
	    // safe to ignore it.
	    name = UNDEFINEDNAME;
	}

	String trimmedName = name.trim();
	return new UnicodeNamePair(characterString, trimmedName);
    }

    /**
     * @param nextLine
     * @param idx
     * @return element on index in String array {@code nextLine} if the array
     *         has a minimal length of {@code idx + 1}. Otherwise an empty
     *         String is returned.
     */
    private String getChecked(String[] nextLine, int idx) {
	return nextLine.length > idx ? nextLine[idx] : "";
    }

    static class UnicodeNamePair {

	public UnicodeNamePair(String characterString, String name) {
	    this.characterString = characterString;
	    this.name = name;
	}

	public final String characterString;
	public final String name;

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (obj == this) {
		return true;
	    }
	    if (obj.getClass() != getClass()) {
		return false;
	    }
	    UnicodeNamePair rhs = (UnicodeNamePair) obj;
	    return new EqualsBuilder()
		    .append(characterString, rhs.characterString)
		    .append(name, rhs.name).isEquals();
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder().append(characterString).append(name)
		    .toHashCode();
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(UnicodeNamePair.this,
		    ToStringStyle.SIMPLE_STYLE);
	}

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
    static String newString(int codePoint) {
	if (Character.charCount(codePoint) == 1) {
	    return String.valueOf((char) codePoint);
	} else {
	    return new String(Character.toChars(codePoint));
	}
    }

    public String unicodeToName(String input) {
	String output = this.unicodeToName.get(input);

	return output;
    }

    public String nameToUnicode(String input) {
	String output = this.nameToUnicode.get(input);
	return output;
    }

}
