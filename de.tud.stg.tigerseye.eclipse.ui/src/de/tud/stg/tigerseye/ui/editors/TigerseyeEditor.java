package de.tud.stg.tigerseye.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.codehaus.groovy.eclipse.core.util.ReflectionUtils;
import org.codehaus.groovy.eclipse.editor.GroovyConfiguration;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.editor.GroovyTagScanner;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;
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

	List<DSLWordRule> activeTigerseyeRules = new ArrayList<DSLWordRule>();
	List<IRule> otherRules = new ArrayList<IRule>();

	for (IRule rule : installedRules) {
	    if (rule instanceof DSLWordRule) {
		DSLWordRule dslWordRule = (DSLWordRule) rule;
		DSLDefinition dsl = dslWordRule.getDsl();
		if (FileType.JAVA.equals(this.fileType) // TODO: (*)
			|| this.activeDSLs.contains(dsl))
		    /*
		     * TODO: (*) Currently, we do not know what DSLs are
		     * selected for a Java file, thus we cannot do context
		     * specific selection of highlighted keyword (for only
		     * selected DSL keywords). As a work around, in Java files,
		     * we highlight keywords of all active DSLs
		     */
		    activeTigerseyeRules.add(dslWordRule);
	    } else
		otherRules.add(rule);
	}

	if (activeTigerseyeRules.size() > 0) {
	    CombinedWordRule combinedWordRule = getCombinedWordRule(otherRules);
	    if (combinedWordRule != null) {
		for (DSLWordRule dslRule : activeTigerseyeRules) {
		    WordMatcher dslMatcher = new CombinedWordRule.WordMatcher();
		    Set<Entry<String, IToken>> wordConfiguration = dslRule
			    .getWordConfiguration().entrySet();
		    for (Entry<String, IToken> entry : wordConfiguration) {
			dslMatcher.addWord(entry.getKey(), entry.getValue());
		    }
		    combinedWordRule.addWordMatcher(dslMatcher);
		}
		scanner.setRules(otherRules.toArray(new IRule[0]));
	    } else { // Old Implementation should be delete-able
		logger.error("An older unexpected implementation has been used. Instead of adding to the common word rules additional coloring Rules  will be used and those by Tigerseye prioritized.");
		List<IRule> mergedRules = new ArrayList<IRule>();
		// put tigerseye Rules _before_ groovy rules.
		mergedRules.addAll(activeTigerseyeRules);
		mergedRules.addAll(otherRules);
		IRule[] filteredSortedRules = mergedRules.toArray(new IRule[0]);
		scanner.setRules(filteredSortedRules);
	    }
	}
    }

    /**
     * @param otherRules
     * @return first found object of type {@link CombinedWordRule} or
     *         <code>null</code>.
     */
    private @CheckForNull
    CombinedWordRule getCombinedWordRule(List<IRule> otherRules) {
	for (IRule iRule : otherRules) {
	    if (iRule instanceof CombinedWordRule)
		return (CombinedWordRule) iRule;
	}
	return null;
    }

    private void setInvolvedDSLs() {
	IFile file = (IFile) getAdapter(IFile.class);
	String[] extensionsForSrcResource = new DSLExtensionsExtractor()
		.getExtensionsForSrcResource(file.getName());
	logger.info("For file {} extracted extensions {} ", file.getName(),
		Arrays.toString(extensionsForSrcResource));
	Set<DSLDefinition> activeDSLSet = new HashSet<DSLDefinition>();
	ILanguageProvider languageProvider = TigerseyeCore
		.getLanguageProvider();
	for (String extension : extensionsForSrcResource) {
	    DSLDefinition dslForExtension = languageProvider
		    .getActiveDSLForExtension(extension);
	    if (dslForExtension != null) {
		activeDSLSet.add(dslForExtension);
	    }
	}
	this.fileType = FileTypeHelper.getTypeForSrcResource(file.getName());
	this.activeDSLs = activeDSLSet;
    }

    /*
     * Have to override default groovy behavior, sinceI have no
     * GroovyCompilationUnit to return. That causes a null pointer exception
     * since no check is made for a non null GroovyCompilationUnit
     */
    @Override
    protected JavaOutlinePage createOutlinePage() {
	GroovyCompilationUnit gcu = getGroovyCompilationUnit();
	if (gcu == null) {
	    JavaOutlinePage page = new JavaOutlinePage(fOutlinerContextMenuId,
		    this);
	    setOutlinePageInput(page, getEditorInput());
	    return page;
	}
	return super.createOutlinePage();
    }

}
