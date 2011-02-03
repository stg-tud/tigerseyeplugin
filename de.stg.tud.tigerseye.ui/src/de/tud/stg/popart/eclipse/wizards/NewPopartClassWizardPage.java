package de.tud.stg.popart.eclipse.wizards;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;
import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator.IndentationDirection;
import legacy.org.codehaus.groovy.eclipse.ui.CodeGeneration;
import legacy.org.codehaus.groovy.eclipse.ui.ImportManager;
import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.apache.commons.lang.UnhandledException;
import org.codehaus.groovy.eclipse.wizards.NewClassWizardPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.KeyWordExtractor;
import de.tud.stg.tigerseye.eclipse.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeImage;

/**
 * NewPopartClassWizardPage is a wizard page for creating Popart classes.
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 * @author Leo Roos
 */
public class NewPopartClassWizardPage extends NewClassWizardPage {

    private static final Logger logger = LoggerFactory
	    .getLogger(NewPopartClassWizardPage.class);
    private IJavaProject project;
    private final ArtifactCodeGenerator codeGenerator;
    private final ImportManager importManager;
    private Combo combo;
    private final Map<String, DSLDefinition> dsls = new HashMap<String, DSLDefinition>();

    public NewPopartClassWizardPage(Set<DSLDefinition> activeDSLs) {
	super();
	setTitle("Tigerseye Class");
	setDescription("Create a new Tigerseye class");
	codeGenerator = new ArtifactCodeGenerator(project);
	importManager = new ImportManager(project);
	for (DSLDefinition dsl : activeDSLs) {
	    String extension;
	    try {
		extension = dsl.getValue(DSLKey.EXTENSION);
		dsls.put(extension, dsl);
	    } catch (NoLegalPropertyFound e) {
		// Can be safely ignored
	    }
	}

    }

    @Override
    public Image getImage() {
	return TigerseyeCore.getImage(TigerseyeImage.FileTypeTigerseye64)
		.createImage();
    }

    public Map<String, DSLDefinition> getDsls() {
	return dsls;
    }

    @Override
    public void init(IStructuredSelection selection) {
	super.init(selection);
	IJavaElement jelem = getInitialJavaElement(selection);
	if (jelem == null)
	    MessageDialog.openInformation(getShell(), "Need selection",
		    "Select the project first where to add the new  class");
	this.project = jelem.getJavaProject();
    }

    public IFile createGroovyType(IProgressMonitor monitor)
	    throws CoreException {

	monitor.beginTask("Creating Tigerseye Class...", 2);
	IPackageFragment packageFragment = getPackageFragment();
	List<String> superInterfaces = getSuperInterfaces();
	getModifiers();

	monitor.worked(1);

	codeGenerator.clear();
	importManager.clear();

	importManager.setBasePackage(getPackageText());

	// Print popart-language information
	printDeclaredMethods(getDsls().get(combo.getText()));

	codeGenerator.addLineBreak().addCode("##imports##").addLineBreak();

	codeGenerator.addCode(
		getPopartExtensionName() + "(name:'" + getTypeName() + "'){")
		.addLineBreak();

	if (isCreateConstructors()) {
	    if (getGroovySuperClass() != null) {
		IType type = project.findType(getGroovySuperClass());
		if (type != null) {
		    codeGenerator
			    .addCode(IndentationDirection.INDENT_RIGHT, "");
		    for (int i = 0; i < type.getMethods().length; i++) {
			IMethod method = type.getMethods()[i];
			int mflags = method.getFlags();

			if (method.isConstructor()
				&& (Flags.isPublic(mflags) || Flags
					.isProtected(mflags))) {
			    CodeGeneration.handleInheritedMethod(project,
				    method, codeGenerator, importManager,
				    getTypeName(), getPackageLabel());
			}
		    }
		} else {
		    logger.warn("Type <" + getGroovySuperClass()
			    + "> not found");
		}
	    } else {
		codeGenerator
			.addCode(
				IndentationDirection.INDENT_RIGHT,
				CodeGeneration.getMethodComment(project,
					getTypeName(), getPackageText(),
					getTypeName(), new String[0], "V"))
			.addCode("public " + getTypeName() + "(){")
			.addCode(
				IndentationDirection.INDENT_RIGHT,
				CodeGeneration
					.getMethodBodyContent(project,
						getTypeName(), getTypeName(),
						false, ""))
			.addCode(IndentationDirection.INDENT_LEFT, "}");
	    }
	    codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "");
	}

	if (isCreateMain()) {
	    codeGenerator
		    .addLineBreak()
		    .addCode(
			    IndentationDirection.INDENT_RIGHT,
			    CodeGeneration.getMethodComment(project,
				    getTypeName(), getPackageText(), "main",
				    new String[] { "args" }, "V"))
		    .addCode("public static void main(def args){")
		    .addCode(
			    IndentationDirection.INDENT_RIGHT,
			    CodeGeneration.getMethodBodyContent(project,
				    getTypeName(), "main", false, ""))
		    .addCode(IndentationDirection.INDENT_LEFT, "}");
	} else {
	    codeGenerator.addCode(IndentationDirection.INDENT_RIGHT, "");
	}
	codeGenerator.addLineBreak();

	if (!superInterfaces.isEmpty() && isCreateInherited()) {
	    for (int i = 0; i < superInterfaces.size(); i++) {
		IType type = project.findType(superInterfaces.get(i));
		if (type != null) {
		    getInheritedMethods(type);
		} else {
		    logger.warn("Type <" + superInterfaces.get(i)
			    + "> not found");
		}
	    }
	}

	if (getGroovySuperClass() != null && isCreateInherited()) {
	    IType type = project.findType(getGroovySuperClass());

	    if (type != null) {
		for (int i = 0; i < type.getMethods().length; i++) {
		    IMethod method = type.getMethods()[i];
		    int mflags1 = method.getFlags();

		    if (Flags.isAbstract(mflags1)
			    && (Flags.isPublic(mflags1) || Flags
				    .isProtected(mflags1))) {
			CodeGeneration.handleInheritedMethod(project, method,
				codeGenerator, importManager, getTypeName(),
				getPackageLabel());
		    }
		}
	    } else {
		logger.warn("Type <" + getGroovySuperClass() + "> not found");
	    }
	}

	codeGenerator.addCode(IndentationDirection.INDENT_LEFT, "}");

	String sourceCode = codeGenerator.toString();
	sourceCode = sourceCode.replaceAll("##imports##",
		importManager.toString());
	sourceCode = sourceCode.replaceAll("##generics##",
		importManager.getGenericTypesAsString());

	monitor.worked(1);

	monitor.done();

	return WizardUtil.createGroovyType(getPackageFragmentRoot(),
		packageFragment, getTypeName() + "." + getPopartExtensionName()
			+ ".dsl", sourceCode);
    }

    private String getGroovySuperClass() {
	logger.error("Accessing no operation supporting method getGroovySuperClass");
	return null;
    }

    private void getInheritedMethods(IType type) throws CoreException {
	IMethod[] methods = type.getMethods();

	for (int i = 0; i < methods.length; i++) {
	    CodeGeneration.handleInheritedMethod(project, methods[i],
		    codeGenerator, importManager, getTypeName(),
		    getPackageLabel());
	}
    }

    private String getPopartExtensionName() {
	if (combo == null)
	    return "";
	return combo.getText();
    }

    @Override
    protected void createTypeNameControls(Composite composite, int columns) {
	super.createTypeNameControls(composite, 3);

	Composite innerComposite = new Composite(composite, SWT.NONE);
	innerComposite.setLayout(new GridLayout(2, false));

	combo = new Combo(innerComposite, SWT.READ_ONLY);
	combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	setLanguagesExtensionFromStore();

	Label b = new Label(innerComposite, SWT.NONE);
	b.setLayoutData(new GridData());
	b.setText(".dsl");

    }

    /**
     * This method adds all existing extensions from store to combo-box
     * selection model, which is used for selection DSL extension by creating of
     * Popart classes.
     */
    private void setLanguagesExtensionFromStore() {
	Collection<DSLDefinition> values = getDsls().values();
	for (DSLDefinition extension : values) {
	    boolean active = extension.isActive();
	    if (active) {
		try {
		    combo.add(extension.getValue(DSLKey.EXTENSION));
		} catch (NoLegalPropertyFound e) {
		    throw new UnhandledException(
			    "Since dsls have already been questioned once for the extension this exception should never be thrown.",
			    e);
		}
	    }
	}
	combo.select(0);
    }

    /**
     * This method prints keywords of language for the given class-path.
     * 
     * @param externalClassPath
     *            the path from a class with methods that should be found
     */
    private void printDeclaredMethods(DSLDefinition dsl) {

	// Print Language classpath
	codeGenerator.addLineBreak().addCode("/**");
	codeGenerator.addCode(" * Tigerseye language: " + dsl.getClassPath());
	codeGenerator.addCode(" *");
	codeGenerator.addCode(" * Declared keywords:");

	// Read all public declared fields from external class
	KeyWordExtractor keyWordExtractor = new KeyWordExtractor(dsl.loadClass());
	Field[] declaredFields = keyWordExtractor
		.getDeclaredLiteralKeywords();
	if (declaredFields != null) {
	    for (Field publicDeclaredField : declaredFields) {

		String printedLine = publicDeclaredField.getName();

		printedLine = publicDeclaredField.getType().getSimpleName()
			+ " " + printedLine;

		codeGenerator.addCode(" *  " + printedLine);
	    }
	}

	// Read all public declared methods from external class
	Method[] declaredMethods = keyWordExtractor.getMethodKeywords();
	if (declaredMethods != null) {
	    for (Method publicDeclaredMethod : declaredMethods) {

		String printedLine = publicDeclaredMethod.getName();

		String parameters = "";
		for (int i = 0; i < publicDeclaredMethod.getParameterTypes().length; i++) {
		    if (i > 0)
			parameters = parameters + ", ";
		    parameters = parameters
			    + publicDeclaredMethod.getParameterTypes()[i]
				    .getSimpleName().toString();
		}

		printedLine = publicDeclaredMethod.getReturnType()
			.getSimpleName()
			+ " "
			+ printedLine
			+ ("(" + parameters + ")");

		codeGenerator.addCode(" *  " + printedLine);

	    }
	}
	codeGenerator.addCode(" */").addLineBreak();
    }

    public @CheckForNull
    IJavaProject getProject() {
	return project;
    }
}