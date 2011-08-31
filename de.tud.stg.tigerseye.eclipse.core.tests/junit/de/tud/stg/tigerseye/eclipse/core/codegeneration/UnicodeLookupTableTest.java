package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable.UnicodeNamePair;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class UnicodeLookupTableTest {

	private static final String controlSampleProperties = "resources/testvals.properties";
	private UnicodeLookupTable cut = getUnicodeTable();

	private static UnicodeLookupTable getUnicodeTable() {
		UnicodeLookupTable cut = TransformationUtils.getDefaultLookupTable();
		return cut;
	}

	@Test
	public void testTransformRarr() {
		String transform = cut.unicodeToName("→");
		assertEquals("rarr", transform);
	}

	@Test
	public void testTransformString() {
		String transform = cut.nameToUnicode("rarr");
		String expected = "→";
		assertEquals(expected, transform);
	}

	@Test
	public void shouldTransformTheSurrogateQfr() throws Exception {
		String transform = cut.nameToUnicode("Qfr");
		String back = cut.unicodeToName(transform);
		assertNotNull(transform);
		assertEquals("Qfr", back);
	}
	
	@Test
	public void shouldFindSemicolon() throws Exception {
		String mathclassex11 = "003B;P;;semi;ISONUM;;SEMICOLON";
		String mathclassex12 = "003B;P;;;semi;ISONUM;;SEMICOLON";
		UnicodeNamePair expectedpair = new UnicodeNamePair(";", "semi");
		shouldReturnExpectedForAddNewUnicodeCharacter(mathclassex11, expectedpair);
		shouldReturnExpectedForAddNewUnicodeCharacter(mathclassex12, expectedpair);
		
	}

	/**
	 * Tests if all elements provided in the testvals.properties file can 
	 * be found key-by-value and value-by-key.
	 */
	@Test
	public void shouldKnowControlSample() {
		Properties testVals = new Properties();
		InputStreamReader inputStreamReader = null;
		try {
			InputStream resourceAsStream = UnicodeLookupTableTest.class
					.getResourceAsStream(controlSampleProperties);
			assertNotNull(resourceAsStream);
			inputStreamReader = new InputStreamReader(
					resourceAsStream, "UTF-8");
			testVals.load(inputStreamReader);
		} catch (IOException e) {
			throw new UnhandledException(e);
		} finally{
			IOUtils.closeQuietly(inputStreamReader);
		}
		Set<Entry<Object, Object>> elements = testVals.entrySet();
		ArrayList<String> keynotfound = new ArrayList<String>();
		ArrayList<String> valuenotfound = new ArrayList<String>();
		for (Entry<Object, Object> entry : elements) {
			String keyAsString = (String) entry.getKey();
			String valueFor = cut.unicodeToName(keyAsString);
			if (valueFor == null)
				keynotfound.add(keyAsString);

			String value = (String) entry.getValue();
			String keyFor = cut.nameToUnicode(value);
			if (keyFor == null)
				valuenotfound.add(value);
		}

		if (!keynotfound.isEmpty()) {
			fail("Could not resolve value for following keys: " + keynotfound);
		}

		if (!valuenotfound.isEmpty()) {
			fail("Could not resolve key for following values: " + valuenotfound);
		}

	}
	
	@Test
	public void shouldRecognizeCorrectCharacterForm() throws Exception {
		String[] characterCodes = {"0021", "0000", "FEFF", "1D7CE", "1014BF" };
		for (String characterCode : characterCodes) {
			int hasValidCharacterCodeForm = new UnicodeLookupTable().hasValidCharacterCodeForm(characterCode);
			boolean isSupported = hasValidCharacterCodeForm >= 0;
			assertTrue (String.format("characterCode for %1s should be supported", characterCode),isSupported );
		}
	}

	@Test
	public void shouldRecognizeUnsupportedCharacterCode() throws Exception {
		String[] characterCodes = {"0021..0031", "1D455=210E", "3sd4", "110000" , "FF10000", "FFFF", "10FFFF"};
		for (String characterCode : characterCodes) {			
			ignoresLineIfCharacterCodeInvalid(characterCode);
		}
	}

	private void ignoresLineIfCharacterCodeInvalid(String characterCode) {
		int hasValidCharacterCodeForm = new UnicodeLookupTable().hasValidCharacterCodeForm(characterCode);
		boolean notSupported = hasValidCharacterCodeForm < 0;
		assertTrue(String.format("characterCode %1s should not be supported", characterCode),notSupported);
	}
	
	@Test
	public void testDontReadCommented() throws Exception {
		String commented = "#\n" + 
				"# Some character positions in the Mathematical Alphanumeric Symbols block are\n" + 
				"# reserved and have been mapped to the Letterlike Symbols block in Unicode.\n" + 
				"# This is indicated in 23 special purpose comments.\n" + 
				"#\n" + 
				"# Updated to reflect character repertoire of Unicode 6.0. For more information\n" + 
				"# see revision 12 or later of UTR #25.\n" + 
				"# ------------------------------------------------\n" + 
				"\n" + 
				"#code point;class;char;entity name;entity set;note/description;CHARACTER NAME\n" + 
				"0020;S;;;;;SPACE\n" + 
				"0021;N;!;excl;ISONUM;Factorial spacing;EXCLAMATION MARK\n" + 
				"0021;N;!;fact;;;FACTORIAL\n" + 
				"0023;N;#;num;ISONUM;;NUMBER SIGN\n" +
				"02DC;D;˜;tilde;;Alias for 0303;SMALL TILDE\n" + 
				"0300;D;̀;;ISODIA;MathML prefers 0060;COMBINING GRAVE ACCENT\n" + 
				"0301;D;́;;ISODIA;MathML prefers 00B4; COMBINING ACUTE ACCENT\n" + 
				"0302;D;̂;;ISODIA;MathML prefers 02C6; COMBINING CIRCUMFLEX ACCENT\n" + 
				"0303;D;̃;;ISODIA;MathML prefers 02DC; COMBINING TILDE\n" + 
				"0304;D;̄;;ISODIA;MathML prefers 00AF; COMBINING MACRON\n";
		UnicodeLookupTable ult = new UnicodeLookupTable();
		ult.load(new StringReader(commented));
		// No error should be thrown
		String[][] code_char_name = {
				{"0020"," ","SPACE"},//
				//{"0021","!","excl"},// ambigious with excl but
				{"0021","!","fact"},// 
				{"0023","#","num"},//
				{"02DC","˜","tilde"}, //
		};
		
		assertEquals("!", ult.nameToUnicode("excl"));
		assertEquals("!", ult.nameToUnicode("fact"));
		
		for (int i = 0; i < code_char_name.length; i++) {
//			String code = code_char_name[i][0];
			String csar = code_char_name[i][1];
			String name = code_char_name[i][2];
			assertEquals(csar, ult.nameToUnicode(name));
			assertEquals(name, ult.unicodeToName(csar));
		}
	}
	
	@Test
	public void testLinesWithLesserElements() throws Exception {
		String invalidLine= "2250;R;≐;esdot";
		UnicodeNamePair expected = new UnicodeNamePair(UnicodeLookupTable.newString(0x2250), "esdot");
		shouldReturnExpectedForAddNewUnicodeCharacter(invalidLine, expected);
	}	

	private void shouldReturnExpectedForAddNewUnicodeCharacter(String line,
			UnicodeNamePair expected) {
		UnicodeNamePair pair = new UnicodeLookupTable().addNewUnicodeCharacter(line);
		assertEquals(expected, pair);
	}
	
	@Test
	public void testAddNewUnicodeCharacterWithStdLine() throws Exception {
		String aLine = "0304;D;̄;;ISODIA;MathML prefers 00AF; COMBINING MACRON\n";
		shouldReturnExpectedForAddNewUnicodeCharacter(aLine, new UnicodeNamePair(UnicodeLookupTable.newString(0x0304), "MathML prefers 00AF"));
	}
	
	@Test
	public void testAddNewUnicodeCharacterWithWindowsEOF() throws Exception {
		String aLine = "0021;N;!;fact;;;FACTORIAL\r\n";
		shouldReturnExpectedForAddNewUnicodeCharacter(aLine, new UnicodeNamePair("!", "fact"));
	}
	
	@Test
	public void testAddNewUnicodeCharacterWithEmptyLine() throws Exception {
		String aLine = "";
		shouldReturnExpectedForAddNewUnicodeCharacter(aLine, null);
	}
	
	@Test
	public void testAddNewUnicodeCharacterWithWindowsNoName() throws Exception {
		String aLine = "0021;N;!;;;";
		shouldReturnExpectedForAddNewUnicodeCharacter(aLine, new UnicodeNamePair("!", UnicodeLookupTable.UNDEFINEDNAME));
	}
	
	

}
