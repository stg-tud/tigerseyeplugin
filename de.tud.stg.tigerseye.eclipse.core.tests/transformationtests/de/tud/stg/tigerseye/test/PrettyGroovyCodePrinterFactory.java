package de.tud.stg.tigerseye.test;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;

public class PrettyGroovyCodePrinterFactory implements CodePrinterFactory {

	@Override
	public CodePrinter createCodePrinter() {
		return new PrettyGroovyCodePrinter();
	}

}
