package de.tud.stg.tigerseye.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.eclipse.core.util.ReflectionUtils;
import org.codehaus.groovy.eclipse.editor.GroovyConfiguration;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.editor.GroovyTagScanner;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.utils.ArraySet;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLExtensionsExtractor;
import de.tud.stg.tigerseye.eclipse.core.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.ui.editors.TigerseyeGroovyEditorHighlightingExtender.DSLWordRule;

public class TigerseyeEditor extends GroovyEditor {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeEditor.class);
    // private ColorManager colorManager;
    //
    // public TigerseyeEditor() {
    // super();
    // colorManager = new ColorManager();
    // setSourceViewerConfiguration(new XMLConfiguration(colorManager));
    // setDocumentProvider(new XMLDocumentProvider());
    // }
    // public void dispose() {
    // colorManager.dispose();
    // super.dispose();
    // }

    private Set<DSLDefinition> activeDSLs;
    private FileType fileType;

    public TigerseyeEditor() {
	super();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
	    throws PartInitException {
	super.init(site, input);
	setInvolvedDSLs();
	prioritizeTigerseyeRules();
    }

    private void prioritizeTigerseyeRules() {
	GroovyConfiguration groovyConfiguration = getGroovyConfiguration();

	GroovyTagScanner codeSanner = (GroovyTagScanner) ReflectionUtils
		.getPrivateField(JavaSourceViewerConfiguration.class,
			"fCodeScanner", groovyConfiguration);
	GroovyTagScanner scanner = codeSanner;

	IRule[] rules = (IRule[]) ReflectionUtils.getPrivateField(
		RuleBasedScanner.class, "fRules", codeSanner);

	List<IRule> tigerseyeRules = new ArrayList<IRule>();
	List<IRule> otherWordRules = new ArrayList<IRule>();
	List<IRule> otherRules = new ArrayList<IRule>();

	for (IRule rule : rules) {
	    if (rule instanceof DSLWordRule) {
		DSLDefinition dsl = ((DSLWordRule) rule).getDsl();
		if (FileType.JAVA.equals(this.fileType)
			|| this.activeDSLs.contains(dsl))
		    tigerseyeRules.add(rule);
	    } else if (rule instanceof WordRule) {
		otherWordRules.add(rule);
	    } else
		otherRules.add(rule);
	}
	if (tigerseyeRules.size() > 0) {
	    List<IRule> mergedRules = new ArrayList<IRule>(otherRules);
	    mergedRules.addAll(tigerseyeRules);
	    mergedRules.addAll(otherWordRules);
	    IRule[] filteredSortedRules = mergedRules.toArray(new IRule[0]);
	    scanner.setRules(filteredSortedRules);
	}
    }

    private void setInvolvedDSLs() {
	IFile file = (IFile) getAdapter(IFile.class);
	String[] extensionsForSrcResource = new DSLExtensionsExtractor()
		.getExtensionsForSrcResource(file.getName());
	logger.info("Found extensions {} ",
		Arrays.toString(extensionsForSrcResource));
	Set<DSLDefinition> activeDSLSet = new ArraySet<DSLDefinition>();
	ILanguageProvider languageProvider = TigerseyeCore
		.getLanguageProvider();
	for (String extension : extensionsForSrcResource) {
	    try {
		DSLDefinition dslForExtension = languageProvider
			.getActiveDSLForExtension(extension);
		activeDSLSet.add(dslForExtension);
	    } catch (DSLNotFoundException e) {
		logger.warn("No active dsl for {}", e.getDSLExtension());
	    }
	}
	this.fileType = FileType.getTypeForSrcResource(file.getName());
	this.activeDSLs = activeDSLSet;
    }

}
