package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators;


import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.ui.ArtifactCodeGenerator;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.ui.ArtifactCodeGenerator.IndentationDirection;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.wizards.WizardUtil;

/**
 * A code generator for PopartStructuredElementKeywords.
 * A PopartOperationKeywordCodeGenerator is capable of generating
 * source code for a Popart language defintion from PopartStructuredElementKeywords.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartStructuredElementKeywordCodeGenerator implements IPopartKeywordCodeGenerator {
	
	private PopartStructuredElementKeyword keyword;
	
	/**
	 * Constructs a new PopartStructuredElementKeywordCodeGenerator for the
	 * specified PopartStructuredElementKeyword.
	 * 
	 * @param operationKeyword The PopartStructuredElementKeyword
	 */
	public PopartStructuredElementKeywordCodeGenerator(PopartStructuredElementKeyword structuredElementKeyword) {
		keyword = structuredElementKeyword;
	}

	/**
	 * @see de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.IPopartKeywordCodeGenerator
	 */
	public void create(IJavaProject project, IPackageFragment packagefragment,
			IPackageFragmentRoot root, ArtifactCodeGenerator codeGenerator) {
		
		String returnType = keyword.getReturnType();
		String name = keyword.getName();
				
		if (returnType==null || returnType.equals("")) {
			keyword.setReturnType("void");
			returnType = keyword.getReturnType();
		}
		else {
			WizardUtil.createType(project, packagefragment, root, returnType, false);
		}
		
		
		codeGenerator.addLineBreak();
		codeGenerator.addCode(IndentationDirection.INDENT_RIGHT, "");
		codeGenerator.addCode(annotation());
		codeGenerator.addCode("public " + returnType + " " + name + "(" + keyword.getParameterString() + ") {");		
		codeGenerator.addLineBreak();
		codeGenerator.addLineBreak();
		codeGenerator.addCode("}");
		codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "");
		
	}
	
	/**
	 * Returns the annotation to set on this keyword.
	 * 
	 * @return The annotation string
	 */
	private String annotation() {
		String result = "@PopartType(clazz=PopartStructuredElementKeyword.class,breakpointPossible=";
		result += (keyword.isBreakpointPossible()) ? 1 : 0;
		result += ")";
		return result;
	}

}
