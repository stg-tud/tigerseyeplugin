package utilities;

import junit.framework.Assert;

public class StringUtils {

	public static void equalsIgnoringWhitspace(String output, String expected) {
		output = removeWhitespaces(output);
		expected = removeWhitespaces(expected);
		Assert.assertEquals(expected, output);
	}

	public static String removeWhitespaces(String astr) {
		return astr.replaceAll("\\s", "");
	}

}
