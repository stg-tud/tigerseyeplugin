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
 * {@link UnicodeLookupTable} stores the mapping between Unicode characters and
 * their textual representation. <br>
 * Use the {@link #load(Readable...)} method to initialize the
 * {@link UnicodeLookupTable} object.
 * <p>
 * The expected file format is described on the <a
 * href="http://www.unicode.org/Public/math/revision-12/MathClassEx-12.txt"
 * ><i>The Unicode Consortium</i> home page</a>. The implementation has been
 * tested with <i>MathClassEx-11.txt</i> and <i>MathClassEx-12.txt</i>.
 * 
 * @author Leo Roos
 * 
 */
public class UnicodeLookupTable {
    private static final String commentLineStartString = "#";

    private static final String splitString = ";";

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
	// For documentation purposes
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
		if (!nextLine.trim().isEmpty()
			&& !nextLine.startsWith(commentLineStartString)) {
		    processNextLine(nextLine);
		}
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
	    logger.debug(
		    "Following line has an unsupported character code (either not supported by JVM or unsupported format): {}",
		    nextLine);
	    return;
	} else {
	    this.unicodeToName.put(uniname.characterString, uniname.name);
	    this.nameToUnicode.put(uniname.name, uniname.characterString);
	}
    }

    /*
     * Returns a new {@link UnicodeNamePair} or null if the character code is
     * not recognized or format was invalid
     */
    @CheckForNull
    UnicodeNamePair addNewUnicodeCharacter(String line) {
	Scanner scanner = new Scanner(line).useDelimiter(splitString);
	String characterCode;
	if (scanner.hasNext()) {
	    characterCode = scanner.next();
	} else {
	    return null;
	}

	int characterInt = hasValidCharacterCodeForm(characterCode);
	if (characterInt < 0)
	    return null;

	String characterString = newString(characterInt);

	String name = "";
	String element = "";
	String elementClass = "";

	int elementNumber = 2;
	while (name.isEmpty()) {
	    if (scanner.hasNext())
		element = scanner.next();
	    else
		element = "";

	    switch (elementNumber) {
	    case 2: // 2: class, one of: N,A,B,C,D,F,G,L,O,P,R,S,U,V,X
		    // not a descriptive representation
		elementClass = element;
		break;
	    case 3: // 3: Unicode character (UTF-8)
		    // XXX(leo;Aug 28, 2011) assertions don't really work. Would
		    // have to assert each special case like non-breaking space
		    // or characters which representation
		    // differ when parsed or read from unicode table file
		    // if (elementClass.equals("S")) {
		    // // some kind of whitespace
		    // } else if (";".equals(characterString)) {
		    // assert "003B".equals(characterCode) :
		    // formatCharacterString(
		    // characterString, characterCode);
		    // } else {
		    // assert element.equals(characterString) :
		    // formatCharacterString(
		    // characterString, characterCode);
		    // }
		break;
	    case 4: // 4: entity name
		/*
		 * XXX This is what we want. Should be perhaps the only option
		 * and undefined otherwise.
		 */
		if (splitString.equals(characterString) && element.isEmpty()) {
		    elementNumber--; /*
				      * Have to try next element to support both
				      * versions, i.e. 11 and 12, of
				      * MatchClassEx.
				      */
		}
		name = element;
		break;
	    case 5: // 5: ISO entity set
		    // not descriptive enough
		break;
	    case 6: // 6: descriptive comments (of various types)
		name = element;
		break;
	    case 7: /*
		     * 7: Unicode 'name' (or name range), in all caps. They are
		     * not normative and may not always match the official
		     * character names.
		     */
		name = element;
		break;
	    default:
		name = UNDEFINEDNAME;
	    }

	    elementNumber++;
	}

	String trimmedName = name.trim();
	return new UnicodeNamePair(characterString, trimmedName);
    }

    private String formatCharacterString(String characterString,
	    String characterCode) {
	return "characterString was [" + characterString + "] with code["
		+ characterCode + "]";
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
     * optimizes for BMP characters. (BMP = Basic Multilingual Plane; the
     * unicode characters in range {@code u+0000 - u+ffff})
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
