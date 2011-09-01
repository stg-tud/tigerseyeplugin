package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionConstants.ProductionElement.*;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionConstants.ProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

public class MethodProductionScannerTest {

	public static interface AccessStrategy<T> {
		T get(MethodProductionElement mpe);
	}

	private MethodProductionScanner testee;

	@Before
	public void beforeEachTest() throws Exception {
		testee = new MethodProductionScanner();
	}

	@Test
	public void shouldbeconfigurablewithparameterescapeAndWhitespaceEscape() throws Exception {
		String pse = "p";
		String wse = "_";
		testee.setParameterEscape(pse);
		testee.setWhitespaceEscape(wse);
	}

	@Test
	public void shouldHaveShortcutConstructorWithPSEAndWSE() throws Exception {
		MethodProductionScanner mps = new MethodProductionScanner("a", "b");
		String wse = mps.getWhitespaceEscape();
		String pse = mps.getParameterEscape();
		assertThat(wse).isEqualTo("a");
		assertThat(pse).isEqualTo("b");
	}

	@Test
	public void shouldHaveDefaultConstructor() throws Exception {
		MethodProductionScanner mps = new MethodProductionScanner();
		String wse = mps.getWhitespaceEscape();
		String pse = mps.getParameterEscape();
		assertThat(wse).isEqualTo(ConfigurationOptions.WHITESPACE_ESCAPE.defaultValue);
		assertThat(pse).isEqualTo(ConfigurationOptions.PARAMETER_ESCAPE.defaultValue);
	}

	@Test
	public void shouldBeReusableForOtherProductions() throws Exception {
		String currentProduction = "SELECT__p0__FROM__p1";
		testee.startScan(currentProduction);
		String actcurProd = testee.getCurrentProduction();
		assertThat(actcurProd).isEqualTo(currentProduction);
		testee.startScan("nextProduction");
		actcurProd = testee.getCurrentProduction();
		assertThat(actcurProd).isEqualTo("nextProduction");
	}
	
	@Test
	public void shouldBeginFromTestAfterReset() throws Exception {
		String currentProduction = "SELECT__p0__FROM__p1";
		testee.startScan(currentProduction);
		assertThat(testee.next().getCapturedString()).isEqualTo("SELECT");
		testee.reset();
		assertThat(testee.next().getCapturedString()).isEqualTo("SELECT");
	}

	@Test(expected = AssertionFailedException.class)
	public void shouldNotAllowNullAsProduction() throws Exception {
		testee.startScan(null);
	}

	@Ignore("I'll think about how much sense that makes")
	@Test
	public void shouldThrowNullPointerIfNoProductionIsSet() throws Exception {
		fail("ThrowNullPointerIfNoProductionIsSet has yet to be written.");
	}

	@Test
	public void shouldTellMeIfMoreIsThereToProcess() throws Exception {
		boolean has = testee.hasNext();
		assertThat(has).isFalse();
	}

	@Test
	public void shouldBeIterable() throws Exception {
		for (MethodProductionElement next : testee) {
			// Just asserting construct
			assertThat(next).isNotNull();
		}
	}

	@Test
	public void shouldReturnKeywordIfOnlyKeywordProduction() throws Exception {
		testee.startScan("hallo");
		assertThat(testee.hasNext()).isTrue();
		assertThat(testee.next().getCapturedString()).isEqualTo("hallo");
		assertThat(testee.hasNext()).isFalse();
	}

	@Test
	public void shouldHaveCompleteProductionWhileIterating() throws Exception {
		String curProd = "SELECT__p0__FROM__p1";
		testee.startScan(curProd);
		for (MethodProductionElement iterable_element : testee) {
			assertThat(iterable_element).isNotNull();
		}
		String afterProcess = testee.getCurrentProduction();
		assertThat(afterProcess).isEqualTo(curProd);
	}

	@Test
	public void shouldProcessDefaultValuesDirectly() throws Exception {
		testee.startScan("SELECT__p0__FROM__p1__WHERE__p2");
		String[] expectedStringSequence = { "SELECT", "__", "p0", "__", "FROM", "__", "p1", "__", "WHERE", "__", "p2" };
		assertTesteeHasExactlyThatSequence(new CapString(), expectedStringSequence);
	}

	private <T> void assertTesteeHasExactlyThatSequence(AccessStrategy<T> as, T... expectedSequence) {
		for (T el : expectedSequence) {
			assertThat(as.get(testee.next())).isEqualTo(el);
		}
		assertThat(testee.hasNext()).isFalse();
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionIfWSEChangedAfterStart() throws Exception {
		testee.startScan("something");
		testee.setWhitespaceEscape("");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionIfPEChangedAfterStart() throws Exception {
		testee.startScan("something");
		testee.setParameterEscape("z");
	}

	@Test
	public void shouldProcessSequenceOfCustomWSE() throws Exception {
		testee.setWhitespaceEscape(" ");
		testee.startScan("module p0 p1 p2");
		assertTesteeHasExactlyThatSequence(new CapString(), "module", " ", "p0", " ", "p1", " ", "p2");
	}

	private static class CapString implements AccessStrategy<String> {

		@Override
		public String get(MethodProductionElement mpe) {
			return mpe.getCapturedString();
		}

	}

	private static class ProdEl implements AccessStrategy<ProductionElement> {

		@Override
		public ProductionElement get(MethodProductionElement mpe) {
			return mpe.getProductionElementType();
		}

	}

	@Test
	public void shouldHaveCorrectSequenceForCustomParameter() throws Exception {
		testee.startScan("a1__zu__a2__und_a3");
		assertTesteeHasExactlyThatSequence(new CapString(), "a1", "__", "zu", "__", "a2", "__", "und", "_", "a3");
	}

	@Test
	public void shouldHaveCorrectMethodElements() throws Exception {
		testee.startScan("SELECT__p0__FROM__p1__WHERE__p2");
		ProductionElement[] expectedStringSequence = { Keyword, Whitespace, Parameter, Whitespace, Keyword, Whitespace,
				Parameter, Whitespace, Keyword, Whitespace, Parameter };
		assertTesteeHasExactlyThatSequence(new ProdEl(), expectedStringSequence);
	}

	@Test
	public void shouldProcessSimpleKeyword() throws Exception {
		testee.startScan("hallo");
		MethodProductionElement next = testee.next();
		assertThat(next.getCapturedString()).isEqualTo("hallo");
		assertThat(next.getProductionElementType()).isEqualTo(Keyword);
	}

	@Test
	public void shouldhaveNoElementsForEmptyString() throws Exception {
		testee.startScan("");
		assertThat(testee.hasNext()).isFalse();
	}

	@Test(expected = IllegalStateException.class)
	public void shouldhaveThrowExceptionIfNoMoreElements() throws Exception {
		testee.startScan("");
		assertThat(testee.next()).isEqualTo("");
	}

	@Test
	public void shouldProcessAnotherExample() throws Exception {
		testee.setWhitespaceEscape(" ");
		testee.startScan("[ p0 , p1 : p2 ]");
		assertTesteeHasExactlyThatSequence(new CapString(), "[", " ", "p0", " ", ",", " ", "p1", " ", ":", " ", "p2",
				" ", "]");
		testee.reset();
		assertTesteeHasExactlyThatSequence(new ProdEl(), Keyword, Whitespace, Parameter, Whitespace, Keyword,
				Whitespace, Parameter, Whitespace, Keyword, Whitespace, Parameter, Whitespace, Keyword);
	}
	
	@Test
	public void shouldSupportMultiCharacterParameters() throws Exception {
		testee.setParameterEscape("wow");
		testee.startScan("awow3wow4 b wow1");
		assertTesteeHasExactlyThatSequence(new CapString(), "a" ,"wow3", "wow4" ," b ","wow1");
		testee.reset();
		assertTesteeHasExactlyThatSequence(new ProdEl(), Keyword, Parameter, Parameter, Keyword, Parameter);
	}
	
	@Test
	public void shouldSupportMultCharacterWhitespaceStrings() throws Exception {
		testee.setWhitespaceEscape("wow");
		testee.startScan("awowwow b wow");
		assertTesteeHasExactlyThatSequence(new CapString(), "a" ,"wowwow" ," b ","wow");
		testee.reset();
		assertTesteeHasExactlyThatSequence(new ProdEl(), Keyword, Whitespace, Keyword, Whitespace);
	}

	// SELECT__p0__FROM__p1
	// SELECT__p0__FROM__p1__WHERE__p2
	// module p0 p1 p2
	// module p0 [ p1 ] p2 p3
	// p0 /\\ p1
	// { p0 p1 } *
	// ( p0 => p1 )
	// [ p0 , p1 : p2 ]
}
