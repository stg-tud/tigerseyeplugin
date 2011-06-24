package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import java.util.ArrayList;
import java.util.Iterator;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.ui.ArtifactCodeGenerator;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.ui.ArtifactCodeGenerator.IndentationDirection;
import de.tud.stg.popart.eclipse.wizards.legacygroovy.wizards.WizardUtil;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartKeywordCodeGeneratorFactory;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.IPopartKeywordCodeGenerator;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCoreActivator;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeImage;
import de.tud.stg.tigerseye.ui.TigerseyeUIActivator;

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
	setDescription("Create a new Tigerseye language definition.");
	setTitle("Tigerseye Language Definition");
	}

	@Override
	public void init(IStructuredSelection selection) {
		super.init(selection);
		IJavaElement jelem = getInitialJavaElement(selection);
	if (jelem != null)
		this.project = jelem.getJavaProject();
	}

	
	public IFile createLanguageClass() throws CoreException {
		IPackageFragment packageFragment = getPackageFragment();
		String sourceCode = codeGenerator.toString();
		return WizardUtil.createGroovyType(getPackageFragmentRoot(),
				packageFragment, getTypeName() + ".groovy", sourceCode);
    }

    @Override
    public IPackageFragment getPackageFragment() {
	IPackageFragment packageFragment = super.getPackageFragment();
	return packageFragment;
	}


    @Override
    public Image getImage() {
	return TigerseyeCoreActivator.getTigerseyeImage(TigerseyeImage.FileTypeTigerseye64)
		.createImage();
    }

	/**
     * Invokes create on each keyword. Literals are created and Operations /
     * Nested Elements write themselves into the code generator
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

    @Override
    protected IStatus packageChanged() {
	IStatus packageChanged = super.packageChanged();
	if (packageChanged.getSeverity() == IStatus.ERROR)
	    return packageChanged;

	String packageText = getPackageText();
	if (packageText.isEmpty()) {
	    String msg = "<default> package is not allowed for language definitions.";
	    return new Status(IStatus.ERROR, TigerseyeUIActivator.PLUGIN_ID,
		    msg);
	}

	return packageChanged;
	}
}
