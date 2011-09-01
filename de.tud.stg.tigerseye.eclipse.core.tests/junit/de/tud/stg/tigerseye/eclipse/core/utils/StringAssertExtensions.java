package de.tud.stg.tigerseye.eclipse.core.utils;

import static de.tud.stg.tigerseye.util.Utils.newList;
import static org.fest.assertions.Formatting.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import junit.framework.Assert;

import org.fest.assertions.StringAssert;
import org.junit.ComparisonFailure;

public class StringAssertExtensions extends StringAssert {

	private boolean ignoreCase = false;

	protected StringAssertExtensions(String actual) {
		super(actual);
	}

	public StringAssertExtensions containsAllSubstrings(String... substrings) {
		isNotNull();

		ArrayList<String> notContained = getAllNotContained(substrings);

		if (notContained.isEmpty()) {
			return this;
		} else {
			String reason = String.format("Expected string <%s> to contain all of <%s>\nbut did not contain<%s>",
					actual, Arrays.toString(substrings), notContained.toString());
			fail(reason);
		}
		return this;
	}

	private ArrayList<String> getAllNotContained(String... substrings) {
		ArrayList<String> notContained = new ArrayList<String>();
		for (String string : substrings) {
			boolean contains;
			if (ignoreCase) {
				contains = actual.toLowerCase(Locale.ENGLISH).contains(string.toLowerCase(Locale.ENGLISH));
			} else {
				contains = actual.contains(string);
			}
			if (!contains) {
				notContained.add(string);
			}
		}
		return notContained;
	}

	public StringAssertExtensions containsAllSubstringsIgnoreCase(String... substrings) {
		this.ignoreCase = true;
		return containsAllSubstrings(substrings);
	}

	public StringAssertExtensions isEqualToIgnoringWhitespace(String expected) {
		if (actual == null && expected == null)
			return this;
		isNotNull();
		if (equalsIgnoringWhitspace(expected,actual))
			return this;
		failIfCustomMessageIsSet();
		throw new ComparisonFailure("should be equal ignoring whitespace\n", expected,actual);
	}
	

	protected final boolean equalsIgnoringWhitspace(String actual, String expected) {
		expected = removeWhitespaces(expected);
		actual = removeWhitespaces(actual);
		return expected.equals(actual);
	}

	protected final String removeWhitespaces(String astr) {
		return astr.replaceAll("[\\s\\r\\n\\t]", "");
	}

}
