package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class MethodProductionElementTest {

	private KeywordElement keywordElement;
	private WhitespaceElement whitespaceElement;
	private ParameterElement parameterElement;
	private MethodProductionElement[] elementsArray;

	@Before
	public void beforeEachTest() throws Exception {
		keywordElement = new KeywordElement().setCapturedString("somekeyword");
		whitespaceElement = new WhitespaceElement().setCapturedAndEscape("__", "_");
		parameterElement = new ParameterElement().setCapturedAndEscape("p2", "p");
		elementsArray = new MethodProductionElement[] { keywordElement, whitespaceElement, parameterElement };
	}

	@Test
	public void shouldSupportCorrectCapturedString() throws Exception {
		String[] exp = { "somekeyword", "__", "p2" };
		for (int i = 0; i < exp.length; i++) {
			String nextexp = exp[i];
			assertThat(elementsArray[i].getCapturedString()).isEqualTo(nextexp);
		}
	}

	@Test
	public void shouldAllThrowIllegalState() throws Exception {
		MethodProductionElement[] uninits = { new KeywordElement(), new WhitespaceElement(), new KeywordElement() };
		for (MethodProductionElement iterable_element : uninits) {
			try {
				iterable_element.getCapturedString();
				fail("expected " + iterable_element + " to throw exception");
			} catch (IllegalStateException e) {
				// $EXPECTED EXCEPTION$
			}
		}
	}

	@Test
	public void wse_shouldProvideOriginalWSE() throws Exception {
		assertThat(whitespaceElement.getWhitespaceEscape()).isEqualTo("_");
	}

	@Test
	public void pe_shouldProvideOriginalPE() throws Exception {
		assertThat(parameterElement.getParameterEscape()).isEqualTo("p");
	}

	@Test
	public void shouldBeAbleToDecideIfWhitespaceIsOptional() throws Exception {
		assertThat(whitespaceElement.isOptional()).isTrue();
	}

	@Test
	public void shouldBeAbleToDecideIfWhitespaceIsOptional_False() throws Exception {
		WhitespaceElement we = new WhitespaceElement().setCapturedAndEscape(" ", " ");
		assertThat(we.isOptional()).isFalse();
	}

	@Test
	public void wse_shouldDecideOptionalWithMultiCharWSE() throws Exception {
		WhitespaceElement we = new WhitespaceElement().setCapturedAndEscape("zuulzuul", "zuul");
		assertThat(we.isOptional()).isTrue();
	}

	@Test
	public void wse_shouldDecideOptionalWithMultiCharWSE_FALSE() throws Exception {
		WhitespaceElement we = new WhitespaceElement().setCapturedAndEscape("€$", "€$");
		assertThat(we.isOptional()).isFalse();
	}

	@Test(expected = IllegalArgumentException.class)
	public void wse_shouldThrowExceptionIfInputsInconsistent() throws Exception {
		new WhitespaceElement().setCapturedAndEscape("€$u", "€$");
	}

	@Test
	public void pe_shouldReturnParameterNumber() throws Exception {
		ParameterElement pe = new ParameterElement().setCapturedAndEscape("asd34", "asd");
		int parsedParameterNumber = pe.getParsedParameterNumber();
		assertThat(parsedParameterNumber).isEqualTo(34);
	}
	
	@Test(expected=NumberFormatException.class)
	public void pe_shouldThrowNumberExceptionOnIllegalNumber() throws Exception {
		parameterElement.setCapturedAndEscape("pr", "p");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pe_shouldThrowNumberExceptionOnIllegalCombination() throws Exception {
		parameterElement.setCapturedAndEscape("pr67", "po");
	}
}
