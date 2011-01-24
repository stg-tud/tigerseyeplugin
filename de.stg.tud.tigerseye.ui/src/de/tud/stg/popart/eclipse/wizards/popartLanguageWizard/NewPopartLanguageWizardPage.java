package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import java.util.ArrayList;
import java.util.Iterator;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;
import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator.IndentationDirection;
import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.IPopartKeywordCodeGenerator;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartKeywordCodeGeneratorFactory;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;

/**
 * First Page in the PopartLanguageWizard
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class NewPopartLanguageWizardPage extends NewClassWizardPage {

	private final ArtifactCodeGenerator codeGenerator;
	private IJavaProject project;

	public NewPopartLanguageWizardPage() {
		codeGenerator = new ArtifactCodeGenerator(project);
		setDescription("Create a new popart language definition");
		setTitle("popart language definition");
	}

	@Override
	public void init(IStructuredSelection selection) {
		super.init(selection);
		IJavaElement jelem = getInitialJavaElement(selection);
		this.project = jelem.getJavaProject();
	}

	
	public IFile createLanguageClass() throws CoreException {
		IPackageFragment packageFragment = getPackageFragment();
		String sourceCode = codeGenerator.toString();
		return WizardUtil.createGroovyType(getPackageFragmentRoot(),
				packageFragment, getTypeName() + ".groovy", sourceCode);

	}

	/**
	 * Invokes create on each keyword. Literals are created and Operations /
	 * Nested Element write themself into the code generator
	 */
	public void createKewords() {

		codeGenerator.addCode("import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;");
		codeGenerator.addCode("import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;");
		codeGenerator.addCode("import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;");
		codeGenerator.addCode("import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;");
		
		codeGenerator.addLineBreak();
		codeGenerator.addCode("public class "+getTypeName()+" {");
		codeGenerator.addLineBreak();
		codeGenerator.addCode(IndentationDirection.INDENT_RIGHT, "");
		codeGenerator.addCode("public Object eval(HashMap map, Closure cl) {");
		codeGenerator.addCode(IndentationDirection.INDENT_RIGHT, "");
		codeGenerator.addCode("cl.delegate = this;");
		codeGenerator.addCode("cl.resolveStrategy = Closure.DELEGATE_FIRST;");
		codeGenerator.addCode("cl.call();");
		codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "");
		codeGenerator.addCode("}");
		codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "");
		
		codeGenerator.addLineBreak();
		
		
		ArrayList<PopartKeyword> keywords = PopartLanguageModel.getInstance().getKeywordList();
		for (Iterator<PopartKeyword> iterator = keywords.iterator(); iterator
				.hasNext();) {
			PopartKeyword keyword = iterator.next();
			
			IPopartKeywordCodeGenerator keywordCodeGenerator = PopartKeywordCodeGeneratorFactory.getCodeGenerator(keyword);
			keywordCodeGenerator.create(project, getPackageFragment(),
					getPackageFragmentRoot(), codeGenerator);
		}
		
		codeGenerator.addCode("}");
		
		
	}

}
