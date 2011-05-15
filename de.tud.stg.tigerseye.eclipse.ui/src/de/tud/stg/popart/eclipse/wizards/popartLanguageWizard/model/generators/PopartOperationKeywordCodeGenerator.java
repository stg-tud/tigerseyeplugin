package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;
import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator.IndentationDirection;
import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * A code generator for PopartOperationKeywords.
 * A PopartOperationKeywordCodeGenerator is capable of generating
 * source code for a Popart language defintion from PopartOperationKeywords.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartOperationKeywordCodeGenerator implements IPopartKeywordCodeGenerator {
	
	private PopartOperationKeyword keyword;
	
	/**
	 * Constructs a new PopartOperationKeywordCodeGenerator for the
	 * specified PopartOperationKeyword.
	 * 
	 * @param operationKeyword The PopartOperationKeyword
	 */
	public PopartOperationKeywordCodeGenerator(PopartOperationKeyword operationKeyword) {
		this.keyword = operationKeyword;
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
		codeGenerator.addCode(
				"public " + returnType + " " + name + "("
						+ keyword.getParameterString() + ") {");
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
		String result = "@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=";
		result += (keyword.isBreakpointPossible()) ? 1 : 0;
		result += ")";
		return result;
	}

}
