package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;


import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;

public class PurificationReinserterTest {

	private static final Context dummyc = new Context("dummy");
	private PurificationReinserter pr;

	@Before
	public void setUp() throws Exception {
		pr = new PurificationReinserter();
	}
	
	@Test
	public void shouldPrependCode() throws Exception {
		String expected = "this should end up beforethis is the code";
		String pre   ="this should end up before";
		String input = "this is the code";
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(PurificationExtractor.PURIFICATION_EXTRACTED_STRING_KEY, pre);
		String result = pr.transform(dummyc, input, data);				
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void shouldNotPrependAnythingIfNoDataInMap() throws Exception {
		String input = "this is the code";
		String result = pr.transform(dummyc, input, new HashMap<String, Object>());				
		assertThat(result).isEqualTo(input);
	}

}
