package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.tigerseye.test.TestUtils;

public class UnicodeLookupTableTest {

	private static UnicodeLookupTable cut = getUnicodeTable();

	private static UnicodeLookupTable getUnicodeTable() {
		try {
			return new UnicodeLookupTable(TestUtils.MATH_CLASS_EX11);
		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		}
	}

	@Test
	public void testTransformChar() {
		String transform = cut.unicodeCharToName("→");
		assertEquals("rarr",transform);
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
		String back = cut.unicodeCharToName(transform);		
		assertNotNull(transform);
		assertEquals("Qfr", back);
	}
	
	@Test
	public void shouldKnowControlSample(){
		Properties testVals = new Properties();
		try {
			InputStream resourceAsStream = UnicodeLookupTableTest.class.getResourceAsStream("testvals.properties");
			assertNotNull(resourceAsStream);
			//should use Reader to avoid encoding problems 
			InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "UTF-8");
			testVals.load(inputStreamReader);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
		Set<Entry<Object, Object>> elements = testVals.entrySet();
		ArrayList<String> keynotfound = new ArrayList<String>();
		ArrayList<String> valuenotfound = new ArrayList<String>();
		for (Entry<Object, Object> entry : elements) {
			String keyAsString = (String)entry.getKey();
			String valueFor = cut.unicodeCharToName(keyAsString);
			if(valueFor == null)
				keynotfound.add(keyAsString);				
			
			String value = (String) entry.getValue();
			String keyFor = cut.nameToUnicode(value);
			if(keyFor == null)
				valuenotfound.add(value);
		}
		
		if(!keynotfound.isEmpty()){
			fail("Could not resolve value for following keys: "+ keynotfound);
		}
		
		if(!valuenotfound.isEmpty()){
			fail("Could not resolve key for following values: "+ valuenotfound);
		}
		
	}
	
	@Test
	public void testDontReadCommented() throws Exception {
		String commented= "#;some commented code";
		UnicodeLookupTable ult = new UnicodeLookupTable(new StringReader(commented));
		//No error should be thrown
		String transform = ult.unicodeCharToName("#");
		// should not be read
		assertNull(transform);		
	}
	
	public static void main(String[] args) {
		SortedMap<String,Charset> availableCharsets = Charset.availableCharsets();
		Set<Entry<String,Charset>> entrySet = availableCharsets.entrySet();
		for (Entry<String, Charset> entry : entrySet) {
			System.out.println(entry);
		}
	}

}
