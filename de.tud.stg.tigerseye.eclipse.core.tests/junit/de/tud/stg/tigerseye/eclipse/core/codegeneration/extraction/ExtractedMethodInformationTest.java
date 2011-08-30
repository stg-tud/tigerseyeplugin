package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;
import static utilities.TigerseyeAssert.collectionPrettyPrint;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utilities.LongrunningTest;

import de.tud.stg.popart.builder.test.dsls.SetDSL;

public class ExtractedMethodInformationTest {

	private List<ExtractedMethodInformation> testees;

	/**
	 * SetDSL methods:
	 * 
	 * <pre>
	 * union(Set, Set)
	 * intersection(Set, Set)
	 * asSet(MyList)
	 * singleElementedList(String)
	 * multiElementedList(String, MyList)
	 * </pre>
	 */

	@Before
	public void beforeEachTest() throws Exception {
		ExtractedClassInforamtion extractedClassInforamtion = new ExtractedClassInforamtion(SetDSL.class);
		extractedClassInforamtion.load(ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
		testees = extractedClassInforamtion.getMethodsInformation();
	}

	public ExtractedMethodInformation getFirstMethodInfoForName(List<ExtractedMethodInformation> mis, String methodname) {
		for (ExtractedMethodInformation e : mis) {
			if (e.getMethod().getName().contains(methodname)) {
				return e;
			}
		}
		throw new IllegalArgumentException(methodname + "not found in " + collectionPrettyPrint(mis));
	}

	@Test
	public void shouldLoad() {
		fail();
		
	}

	@Test
	public void shouldIsAnnotated() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldGetConfigurationOptions() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldExtractedMethodsInformation() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldGetDSLType() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldGetMethod() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldIsToplevelFalse() {
		ExtractedMethodInformation info = getFirstMethodInfoForName(testees, "multiElementedList");
		assertThat(info.isToplevel()).isFalse();
	}
	
	@Test
	public void shouldIsToplevelTrue() {
		ExtractedMethodInformation info = getFirstMethodInfoForName(testees, "union");
		assertThat(info.isToplevel()).isTrue();
	}

	@Test
	public void shouldGetAnnotationParameterOptionsOverInitialMap() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldToString() {
		fail("Not yet implemented");
	}

}
