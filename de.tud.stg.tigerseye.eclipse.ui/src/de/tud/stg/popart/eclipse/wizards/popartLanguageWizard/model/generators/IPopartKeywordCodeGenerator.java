package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * A interface for Popart keyword code generators. A PopartKeywordCodeGenerator
 * is capable of generating source code for a Popart language defintion from
 * PopartKeywords.
 * This class is used by a NewPopartLanguageClassWizzard.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public interface IPopartKeywordCodeGenerator {
	
	/**
	 * Adds the generated code for a specific PopartKeyword to the specified codeGenerator.
	 * 
	 * @param project The current project
	 * @param packagefragment The current package fragment
	 * @param root The fragment root
	 * @param codeGenerator The code generator the generated code should be added to
	 */
	public void create(IJavaProject project, IPackageFragment packagefragment,
			IPackageFragmentRoot root, ArtifactCodeGenerator codeGenerator);

}
