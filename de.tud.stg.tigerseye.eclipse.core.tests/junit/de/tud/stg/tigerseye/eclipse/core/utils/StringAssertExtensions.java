package de.tud.stg.tigerseye.eclipse.core.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.fest.assertions.StringAssert;

public class StringAssertExtensions extends StringAssert{

	protected StringAssertExtensions(String actual) {
		super(actual);
	}

	public StringAssertExtensions containsAllSubstrings(String... substrings) {
		isNotNull();

		ArrayList<String> notContained = new ArrayList<String>();
		for (String string : substrings) {
			boolean contains = actual.contains(string);
			if (!contains)
				notContained.add(string);
		}

		if (notContained.isEmpty()) {
			return this;
		} else {
			String reason = String.format("Expected string <%s> to contain all of <%s>\nbut did not contain<%s>",
					actual, Arrays.toString(substrings), notContained.toString());
			fail(reason);
		}
		return this;
	}

}
