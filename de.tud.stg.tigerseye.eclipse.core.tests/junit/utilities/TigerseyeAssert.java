package utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodDSLInformation;

/**
 * Additional JUnit assertions
 * 
 * @author Leo_Roos
 * 
 */
public class TigerseyeAssert {

	static public void assertEmpty(Collection<MethodDSLInformation> methodsInformation) {
		if (!methodsInformation.isEmpty())
			throw new AssertionError("\nExpected empty collection but collection had \n\t[" + methodsInformation.size()
					+ "] elements. " + TestUtils.collectionPrettyPrint(methodsInformation));
	}

	static public <T> void assertContainsExactly(Collection<T> expected, Collection<T> actual) {
		for (T t : actual) {
			assertTrue("Collection contains unexpected element \n[" + t + "]", expected.contains(t));
		}
		assertEquals("Actual collection contains more elements than expected." + TestUtils.collectionPrettyPrint(actual),
				expected.size(), actual.size());
	}

}
