package de.tud.stg.tigerseye.eclipse.core.runtime;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FileHelperTest {

	FileHelper flw = new FileHelper();
	
	@Test
	public void testIsJar() throws Exception {
		String[] truePaths = {
				"/asdf/adf/asdfal.jar",
				"asöl345jö.jar",
				"asdf.JAR",
				"asfds.jAr",
				"asfdasdf344rfs.jAr",
				"asfdasdf344rfs.some.jAr",
		};
		
		String[] falsePaths = {
			"",
			"adsöflas.ja",
			"adf.other",
			"adf.other"
		};
		
		for (String string : truePaths) {			
			assertTrue("expected true for " + string , flw.isJar(string));
		}
		for (String string : falsePaths) {			
			assertFalse("expected false for " + string, flw.isJar(string));
		}
	}

}
