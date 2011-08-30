package utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ExtractedMethodInformation;

/**
 * Additional JUnit assertions
 * 
 * @author Leo_Roos
 * 
 */
public class TigerseyeAssert {

	static public void assertEmpty(Collection<ExtractedMethodInformation> methodsInformation) {
		if (!methodsInformation.isEmpty())
			throw new AssertionError("\nExpected empty collection but collection had \n\t[" + methodsInformation.size()
					+ "] elements. " + collectionPrettyPrint(methodsInformation));
	}

	public static String collectionPrettyPrint(Collection<?> c) {
		StringBuilder stringBuilder = new StringBuilder("\nElements in collection:\n");
		for (Object element : c) {
			stringBuilder.append(element.toString()).append("\n");
		}
		return stringBuilder.toString();
	}

	static public <T> void assertContainsExactly(Collection<T> expected, Collection<T> actual) {
		for (T t : actual) {
			assertTrue("Collection contains unexpected element \n[" + t + "]", expected.contains(t));
		}
		assertEquals("Actual collection contains more elements than expected." + collectionPrettyPrint(actual),
				expected.size(), actual.size());
	}

	static public ArrayList<String> findElementsOfSubstring(Collection<String> searchee, String substring) {
		ArrayList<String> result = new ArrayList<String>();
		for (String string : searchee) {
			if (string.contains(substring))
				result.add(string);
		}
		return result;
	}

	static public ArrayList<String> findAllElementsContainingAllSubstrings(Collection<String> searchee, String... substring) {
		ArrayList<String> result = new ArrayList<String>();
		for (String string : searchee) {
			if (containsAllSubstrings(string, substring))
				result.add(string);
		}
		return result;
	}

	static public boolean containsAllSubstrings(String string, String[] substring) {
		for (String string2 : substring) {
			if (!string.contains(string2)) {
				return false;
			}
		}
		return true;
	}

}
