package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;
import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator.IndentationDirection;
import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;

/**
 * A code generator for PopartLiteralKeywords.
 * A PopartLiteralKeywordCodeGenerator is capable of generating
 * source code for a Popart language defintion from PopartLiteralKeywords.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLiteralKeywordCodeGenerator implements IPopartKeywordCodeGenerator {

    private static final Logger logger = LoggerFactory
	    .getLogger(PopartLiteralKeywordCodeGenerator.class);

	private final PopartLiteralKeyword keyword;
	
	/**
	 * Constructs a new PopartLiteralKeywordCodeGenerator for the
	 * specified PopartLiteralKeyword.
	 * 
	 * @param literalKeyword The PopartLiteralKeyword
	 */
	public PopartLiteralKeywordCodeGenerator(PopartLiteralKeyword literalKeyword) {
		this.keyword = literalKeyword;
	}

	/**
	 * @see de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.IPopartKeywordCodeGenerator
	 */
	@Override
	public void create(IJavaProject project, IPackageFragment packagefragment,
			IPackageFragmentRoot root, ArtifactCodeGenerator codeGenerator) {

		String name = keyword.getName();
		String type = keyword.getType();
		
		String supertype = "";
		if (type==null || type.equals("")) {
			 supertype = name;
		}
		else {
			supertype = type;
		}
		codeGenerator.addCode(IndentationDirection.INDENT_RIGHT, "");	
		codeGenerator.addCode("public "+supertype+" "+name+" = new "+name+"();");
		codeGenerator.addLineBreak();
		codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "");
		
		ArtifactCodeGenerator myGenerator = new ArtifactCodeGenerator(project);

		// create supertype as abstract class if not already done.
		if (type!=null && !type.equals("")) {
			WizardUtil.createType(project, packagefragment, root, type, true);
		}		
		
		myGenerator.clear();
		
		String temp = "public class " + name;
		if (type!=null && !type.equals("")) {
			temp += " extends " + type;
		}
		temp += " { ";
		myGenerator.addCode(temp);		
		
		myGenerator.addLineBreak();
		myGenerator.addLineBreak();
		myGenerator.addCode("}");

	try {
			WizardUtil.createGroovyType(root, packagefragment,
					name + ".groovy", myGenerator.toString());
	} catch (CoreException e) {
	    logger.error("Wizard Util access.", e);
	}
	}
	
}
