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
import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.utils.DSLExtensionsExtractor;
import de.tud.stg.tigerseye.ui.editors.TigerseyeGroovyEditorHighlightingExtender.DSLWordRule;

public class TigerseyeEditor extends GroovyEditor {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeEditor.class);

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

	GroovyTagScanner scanner = (GroovyTagScanner) ReflectionUtils
		.getPrivateField(JavaSourceViewerConfiguration.class,
			"fCodeScanner", groovyConfiguration);

	IRule[] installedRules = (IRule[]) ReflectionUtils.getPrivateField(
		RuleBasedScanner.class, "fRules", ReflectionUtils
			.getPrivateField(JavaSourceViewerConfiguration.class,
				"fCodeScanner", groovyConfiguration));

	List<IRule> activeTigerseyeRules = new ArrayList<IRule>();
	List<IRule> groovyWordRules = new ArrayList<IRule>();
	List<IRule> otherRules = new ArrayList<IRule>();

	for (IRule rule : installedRules) {
	    if (rule instanceof DSLWordRule) {
		DSLDefinition dsl = ((DSLWordRule) rule).getDsl();
		/*
		 * TODO: (*) Currently, we do not know what DSLs are selected
		 * for a Java file, thus we cannot do context specific selection
		 * of highlighted keyword (for only selected DSL keywords). As a
		 * work around, in Java files, we highlight keywords of all
		 * active DSLs
		 */
		if (FileType.JAVA.equals(this.fileType) // TODO: (*)
			|| this.activeDSLs.contains(dsl))
		    activeTigerseyeRules.add(rule);
	    } else if (rule instanceof WordRule) {
		groovyWordRules.add(rule);
	    } else
		otherRules.add(rule);
	}
	if (activeTigerseyeRules.size() > 0) {
	    List<IRule> mergedRules = new ArrayList<IRule>(otherRules);
	    // put tigerseye Rules _before_ groovy rules.
	    mergedRules.addAll(activeTigerseyeRules);
	    mergedRules.addAll(groovyWordRules);
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
	    DSLDefinition dslForExtension = languageProvider
		    .getActiveDSLForExtension(extension);
	    if (dslForExtension != null) {
		activeDSLSet.add(dslForExtension);
	    }
	}
	this.fileType = FileType.getTypeForSrcResource(file.getName());
	this.activeDSLs = activeDSLSet;
    }

}
