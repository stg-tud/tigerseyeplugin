package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.dialogs.IInputValidator;

public class LengthValidator implements IInputValidator {
	/**
	 * Validates the String. Returns null for no error, or an error message
	 * 
	 * @param newText
	 *            the String to validate
	 * @return String
	 */
	public String isValid(String newText) {
		return (newText.length() > 0) ? null : "";
	}
}
