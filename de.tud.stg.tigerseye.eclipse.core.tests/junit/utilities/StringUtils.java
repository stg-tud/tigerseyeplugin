package utilities;

import junit.framework.Assert;

public class StringUtils {

	public static void equalsIgnoringWhitspace(String expected, String actual) {
		actual = removeWhitespaces(actual);
		expected = removeWhitespaces(expected);
		Assert.assertEquals(expected, actual);
	}

	public static String removeWhitespaces(String astr) {
		return astr.replaceAll("\\s", "");
	}

}
