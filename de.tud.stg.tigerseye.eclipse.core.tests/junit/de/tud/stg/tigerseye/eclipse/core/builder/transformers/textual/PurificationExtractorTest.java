package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;


import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.PackageAndImportExtractionTest.bconciseExample;
import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.PackageAndImportExtractionTest.commentsBefPackAndBetImpBrokByEDSL;
import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.PackageAndImportExtractionTest.commentsBeforePackageAndBetweenImports;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;

public class PurificationExtractorTest {

	private static final Context dummyContext = new Context("nocontext");

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void shouldExtractUntilExpectedPosition() throws Exception {
			String[] inputs = {bconciseExample, commentsBeforePackageAndBetweenImports, commentsBefPackAndBetImpBrokByEDSL};
			
			Map<String,Object> map = new HashMap<String, Object>();
			
			for (String inp : inputs) {
				new PurificationExtractor().transform(dummyContext, inp, map);				
				String result = (String) map.get(PurificationExtractor.PURIFICATION_EXTRACTED_STRING_KEY);
				String finalIndicator = "FINAL_ANCHOR";
				int expLength = inp.lastIndexOf(finalIndicator);
				assertThat(result.trim()).isEqualTo(inp.substring(0, expLength + finalIndicator.length()));			
			}
			
	}
	
	@Test
	public void shouldHaveExpectedCodeLeft() throws Exception {
		String[] inputs = {bconciseExample, commentsBeforePackageAndBetweenImports, commentsBefPackAndBetImpBrokByEDSL};
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		for (String inp : inputs) {
			String result = new PurificationExtractor().transform(dummyContext, inp, map);				
			String finalIndicator = "FINAL_ANCHOR";
			int expLength = inp.lastIndexOf(finalIndicator);
			assertThat(result).isEqualToIgnoringWhitespace(inp.substring(expLength + finalIndicator.length()));
		}
	}
	
	@Test
	public void shouldHandleEmptyString() throws Exception {
		String transform = new PurificationExtractor().transform(dummyContext, "", new HashMap<String, Object>());
		assertThat(transform).isEmpty();
	}

}
