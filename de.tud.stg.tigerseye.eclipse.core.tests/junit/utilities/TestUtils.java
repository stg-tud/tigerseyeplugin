package utilities;

import java.util.ArrayList;
import java.util.Collection;

public class TestUtils {

	public static String collectionPrettyPrint(Collection<?> c) {
		StringBuilder stringBuilder = new StringBuilder("\nElements in collection:\n");
		for (Object element : c) {
			stringBuilder.append(element.toString()).append("\n");
		}
		return stringBuilder.toString();
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
