package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.RegExCollection.*;

//For exploration purposes
public class PackageAndImportExtractionTest {
	
	static final String bconciseExample = getResource("BConciseForSanitizerTransformerTest.dsl");
	static final String commentsBeforePackageAndBetweenImports = getResource("CommentsBeforePackageAndInBetweenImports.dsl");
	static final String commentsBefPackAndBetImpBrokByEDSL = getResource("CommentsBeforePackageAndInBetweenImportsButEDSLForExample.dsl");

	Pattern origin = Pattern.compile("(.*)(package (?:.*?);)(.*?)(?:@EDSL\\(.*?\\))?(.*)", Pattern.DOTALL);
	

	@Test
	public void shouldFindValidPackages() throws Exception {
		String[] valid = {"package de.tud.stg.tigerseye.examples.logo",
		"package de.tu_darmstadt.stg.tigerseye.examples.logo",
		"package de.tud.stg.tigerseye.examples.logo;",
		"package de.tud.stg.tige1rse2ye.examp$les.logo;",
		"package logo;"};
		
		Pattern p = packagePattern;
		
		for (String string : valid) {			
			Matcher m = p.matcher(string);
			if(!m.matches())
				fail("not found:" + string);
		}
	}
	
	@Test
	public void shouldNotFindInvalidPackages() throws Exception {
		String[] valid = {"package 1de.tud.stg.tigerseye.examples.logo",
				"package de.tu_darmstadt.2stg.tigerseye.examples.logo",
				"package de.tud.st€g.tigerseye.examples.logo;",
				"package de.tud.stg.tige1rse2ye.examp$les.*logo;",
				"package",}
		;
		
		Pattern p = packagePattern;
		
		for (String string : valid) {
			Matcher m = p.matcher(string);
			boolean find = m.find();
			if(find && m.end() == string.length())
				fail("should have not detected pattern in " + string);
		}
	}
	
	@Test
	public void shouldDetectBlockComments() throws Exception {
		String inp = bconciseExample;
		Matcher m = blockComment.matcher(inp);
		String[] expectedComments = expectedCommentsFor_shouldDetectComments;
		matcherShouldFindOnInputExpected(inp, m, expectedComments);		
	}

	private void matcherShouldFindOnInputExpected(String inp, Matcher m, String[] expectedComments) {
		int matchnum = 0;
		while(m.find()){
			String exp= expectedComments[matchnum++];
			String substring = inp.substring(m.start(), m.end());
			assertThat(substring).contains(exp);
		}
		assertThat(matchnum).isEqualTo(expectedComments.length);
	}
	
	@Test
	public void shouldDetectLineComments() throws Exception {
		String inp = bconciseExample;
		String[] expectedComments = expectedLineComments_BconciseFile;
		Matcher m = lineComment.matcher(inp);
		matcherShouldFindOnInputExpected(inp, m, expectedComments);		
	}
	
	@Test
	public void shouldRecognizeImports() throws Exception {
		String[] valid = {
				
				"import java.io.IOException;"  ,
				"import java.io.InputStream;"  ,
				"import java.util.regex.Matcher;"  ,
				"import java.util.regex.Pattern;"  ,
				"import java.io.*"  ,
				"import java.io.*;"  ,
				"import java.util.regex.Matcher_$asd;"  ,
				"import java.util.rege3x.Pat2tern;"  ,
				"import static org.junit.Assert.fail"  ,
				"import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;"  ,
				"import static org.junit.Assert.fail;"  ,
				"import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*"  ,
		};
		shouldMatchAllInputsExactly(valid, imports);
	}
	
	private void shouldMatchAllInputsExactly(String[] valid, Pattern staticImports) {
		for (String string : valid) {
			Matcher m = staticImports.matcher(string);
			boolean matches = m.matches();
			if(!matches)
				fail("Didn't match " + string);
		}
	}
	
	@Test
	public void shouldGoonUntilNeitherCommentImportPackageNorWhitespace() throws Exception {
		String[] inputs = {bconciseExample, commentsBeforePackageAndBetweenImports, commentsBefPackAndBetImpBrokByEDSL};
		
		for (String inp : inputs) {
			String result = matchToSanitizeString(inp);
			String finalIndicator = "FINAL_ANCHOR";
			int expLength = inp.lastIndexOf(finalIndicator);
			assertThat(result.trim()).isEqualTo(inp.substring(0, expLength + finalIndicator.length()));			
		}
		
	}

	private String matchToSanitizeString(String input) {
		Pattern whiteSpacePattern = Pattern.compile("\\s+", Pattern.DOTALL);
		
		Pattern[] ps = {whiteSpacePattern, blockComment, lineComment, packagePattern, imports};
				
		List<Matcher> ms = new ArrayList<Matcher>(ps.length);
		for (Pattern pattern : ps) {
			ms.add(pattern.matcher(input));
		}
		
		int pos = 0;
		while(true){
			
			boolean oneMatcherFoundSomething = false;
			for (Matcher m : ms) {
				m.region(pos, input.length());
				if(m.lookingAt()){
					pos = m.end();					
					oneMatcherFoundSomething = true;
					continue;
				}
			}
			if(!oneMatcherFoundSomething){
				break;
			}
					
		}
		String result = input.substring(0, pos);
		return result;
	}

	private static String getResource(String resourceFile) {
		try {
			InputStream inputStream = PackageAndImportExtractionTest.class.getResourceAsStream("resources/" + resourceFile);
			return IOUtils.toString(inputStream);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}
	
	
	private static final String[] expectedCommentsFor_shouldDetectComments =
	{"/**\n" + 
			" * Tigerseye language: de.tud.stg.tigerseye.lang.logo.ConciseLogo\n" + 
			" *\n" + 
			" * Declared keywords:\n" + 
			" *  void bd(int)\n" + 
			" *  void cs()\n" + 
			" *  void fd(int)\n" + 
			" *  void fs()\n" + 
			" *  void ht()\n" + 
			" *  void lt(int)\n" + 
			" *  void pd()\n" + 
			" *  void pu()\n" + 
			" *  void rt(int)\n" + 
			" *  void setpc(int)\n" + 
			" *  void st()\n" + 
			" *  void ts()\n" + 
			" */",
			"/**\n" + 
			" * The Concise Logo language adds shortened names for the operations\n" + 
			" * already supported by Simple Logo.\n" + 
			" */",
			};
	
	private static final String[] expectedLineComments_BconciseFile = {
		"//Should match until Here FINAL_ANCHOR",
		"// This is the example from ASimpleLogoTest",
		"//A similar example in shortened form as to that in ASimpleLogoTest:",};

}
