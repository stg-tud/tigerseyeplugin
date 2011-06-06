package de.tud.stg.tigerseye.test.utils;

import junit.framework.Assert;

public class StringComparison {

	public static void equalsIgnoringWhitspace(String output, String expected) {
		output = removeWhitespaces(output);
		expected = removeWhitespaces(expected);
		Assert.assertEquals(expected, output);
	}

	public static String removeWhitespaces(String astr) {
		return astr.replaceAll("\\s", "");
	}

}
