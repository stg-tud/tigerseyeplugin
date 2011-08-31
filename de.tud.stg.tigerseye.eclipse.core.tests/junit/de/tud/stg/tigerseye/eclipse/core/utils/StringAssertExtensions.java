package de.tud.stg.tigerseye.eclipse.core.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.fest.assertions.StringAssert;

public class StringAssertExtensions extends StringAssert {

	private boolean ignoreCase = false;

	protected StringAssertExtensions(String actual) {
		super(actual);
	}

	public StringAssertExtensions containsAllSubstrings(String... substrings) {
		isNotNull();

		ArrayList<String> notContained = getAllNoteContained(substrings);

		if (notContained.isEmpty()) {
			return this;
		} else {
			String reason = String.format("Expected string <%s> to contain all of <%s>\nbut did not contain<%s>",
					actual, Arrays.toString(substrings), notContained.toString());
			fail(reason);
		}
		return this;
	}

	private ArrayList<String> getAllNoteContained(String... substrings) {
		ArrayList<String> notContained = new ArrayList<String>();
		for (String string : substrings) {
			boolean contains;
			if (ignoreCase) {
				contains = actual.toLowerCase().contains(string.toLowerCase());
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

}
