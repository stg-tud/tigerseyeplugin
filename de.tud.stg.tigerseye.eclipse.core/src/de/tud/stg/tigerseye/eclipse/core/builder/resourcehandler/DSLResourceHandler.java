/**
 *
 */
package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.IChart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformerConfigurationProvider;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.MethodOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;

public abstract class DSLResourceHandler implements IResourceDeltaVisitor {

    private static final Logger logger = LoggerFactory.getLogger(DSLResourceHandler.class);

    private final CodePrinter prettyPrinter;

    private TransformerConfigurationProvider transformerProvider;

    private OutputPathHandler outputPathHandler;

    private UnicodeLookupTable ult;

    public final FileType fileType;

    @Override
    public boolean visit(IResourceDelta delta) {

	IResource aResource = delta.getResource();

	IFile file;
	if (aResource instanceof IFile) {
	    file = (IFile) aResource;
	} else {
	    logger.trace("Skipping resource {}, since not of type IFile", aResource);
	    // Returning true since this will usually mean the resource is a
	    // folder that might contain files that are of interest to me, i.e.
	    // .dsl Files
	    return true;
	}

	int kind = delta.getKind();
	switch (kind) {
	case IResourceDelta.CHANGED:
	    logger.trace("File '{}' changed.", file);
	    return handleChanged(file);
	case IResourceDelta.ADDED:
	    logger.trace("Resource has been added: '{}'", file);
	    break;
	case IResourceDelta.REMOVED:
	    logger.trace(
		    "Resource has been removed: '{}'. Will test whether its Tigerseye dsl file and will delete derived file if it exists.",
		    file);
	    handleRemoved(file);
	    break;
	default:
	    logger.warn("Cannot handle IResourceDelta kind '{}'. Will ignore it.", kind);
	}

	return false;
    }

    private void handleRemoved(IFile file) {
	String fileName = file.getName();
	FileType typeForSrcResource = FileTypeHelper.getTypeForSrcResource(fileName);
	if (FileType.TIGERSEYE.equals(typeForSrcResource)) {
	    OutputPathHandler outputPathHandler = new OutputPathHandler();
	    IFile outputFile = outputPathHandler.getOutputFile(file);
	    if (outputFile == null) {
		logger.info("failed to determine output file for {}, skipping file", file);
		return;
	    }
	    if (outputFile.exists()) {
		try {
		    logger.debug("derived File for {} exists. it is {}. Trying to delete it.", file, outputFile);
		    outputFile.delete(false, null);
		} catch (CoreException e) {
		    logger.error("Could not delete file " + file, e);
		}
	    }
	}
    }

    private boolean handleChanged(IFile file) {

	boolean canHandleDelta = allDSLsForDeltaAreActive(file);
	if (!canHandleDelta)
	    return true;

	try {
	    if (this.isInteresstedIn(file)) {
		handleResource(file);
	    }
	} catch (Exception e) {
	    logger.error("failed vissiting file {}", file, e);
	}
	return true;
    }

    // * FIXME(Leo_Roos;Aug 27, 2011) paritally copied from
    // DSLResourceHandler#handleResource
    // refactor/move so that common logic in single location
    /*
     * @return whether file <code>resource</code> can be handled
     */
    private boolean allDSLsForDeltaAreActive(IFile resource) {

	IFile srcFile = resource;
	FileType filetype = FileTypeHelper.getTypeForSrcResource(srcFile.getName());
	if (filetype == null) {
	    logger.trace("file {} of no interest for transformation", srcFile.getName());
	    return false;
	}
	StringBuffer resourceContent = ResourceHandlingHelper.readResource(srcFile);
	if (resourceContent == null) {
	    logger.error("Skipping unhandled resource {}", srcFile);
	    return false;
	}
	Set<DSLDefinition> dslDefinitions = Collections.emptySet();
	try {
	    dslDefinitions = determineActiveInvolvedDSLs(srcFile, resourceContent);
	} catch (DSLNotFoundException e) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Resource could not be handled. " + srcFile, e);
	    }
	}
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    if (!dslDefinition.isActive()) {
		return false;
	    }
	}
	return true;
    }

    /*
     * should not be cached
     */
    protected ILanguageProvider getLanguageProvider() {
	return TigerseyeCore.getLanguageProvider();
    }


    public abstract boolean isInteresstedIn(IResource resource);

    // private IPreferenceStore tigerseyePreferenceStore;

    public DSLResourceHandler(FileType fileType, CodePrinter prettyPrinter) {
	this.fileType = fileType;
	this.prettyPrinter = prettyPrinter;
	init();
    }

    protected void init() {
	this.ult = TigerseyeCore.getUnicodeLookupTable();
	this.transformerProvider = new TransformerConfigurationProvider(TigerseyeCore.getTransformationProvider());
	this.outputPathHandler = new OutputPathHandler();
	// this.tigerseyePreferenceStore = TigerseyeCore.getPreferences();
    }

    protected TransformerConfigurationProvider getTransformerProvider() {
	return transformerProvider;
    }

    protected OutputPathHandler getOutputPathHandler() {
	return outputPathHandler;
    }

    public void handleResource(IResource resource) {
	StopWatch sw = new StopWatch();
	sw.start();
	logger.debug("handling resource {}", resource);
	if (!(resource instanceof IFile)) {
	    logger.debug("Skipping resource {}, since not of type IFile", resource);
	    return;
	}
	IFile srcFile = (IFile) resource;
	FileType filetype = FileTypeHelper.getTypeForSrcResource(srcFile.getName());
	if (filetype == null) {
	    logger.error("No filetype could be determined for {}", srcFile.getName());
	    return;
	}
	StringBuffer resourceContent = ResourceHandlingHelper.readResource(srcFile);
	if (resourceContent == null) {
	    logger.error("Skipping unhandled resource {}", srcFile);
	    return;
	}
	Set<DSLDefinition> dslDefinitions = Collections.emptySet();
	try {
	    Set<String> determineInvolvedDSLNames = determineInvolvedDSLNames(srcFile, resourceContent);
	    dslDefinitions = getActiveDSLDefinitionsForNames(determineInvolvedDSLNames);
	    if (determineInvolvedDSLNames.size() != dslDefinitions.size()) {
		logger.trace(
			"Skipping resource {} since not all involved DSLs seem to be active. \nDefined DSLs are {}\nbut from those active are {}",
			new Object[] { resource, determineInvolvedDSLNames.toString(), dslDefinitions.toString() });
		return;
	    }
	} catch (DSLNotFoundException e) {
	    logger.debug("Resource {} could not be handled. {}", new Object[] { srcFile, e.noDSLMsg() }, e);
	    return;
	}
	if (dslDefinitions.size() < 1) {
	    // Might be still valid to just output file without changes
	    logger.trace("No DSLs for {} determined. Will not attempt a transformation.");
	    return;
	}
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    if (!dslDefinition.isActive()) {
		logger.trace("Not all DSLs active (e.g. {}), won't transform {}", dslDefinition, resource);
		return;
	    }
	}
	Context context = new Context(resource.getName());
	context.addDSLs(dslDefinitions);
	context.setFiletype(filetype);
	context.setTransformedFile(srcFile);
	IFile outputFile = getOutputPathHandler().getOutputFile(srcFile);
	if (outputFile == null) {
	    logger.error("Can not determine output file for {}", srcFile);
	    return;
	}
	ByteArrayOutputStream transformedContent = getTransformedContent(resourceContent, context);
	if (transformedContent.size() > 0) {
	    this.writeResourceContent(transformedContent, outputFile);
	} else {
	    logger.trace("Transformation for {} was empty. Will not write any change to file.", resource);
	}
	sw.stop();
	logger.info("{} ms took Transformation of {}", sw.getTime(), resource);
    }

    private ByteArrayOutputStream getTransformedContent(StringBuffer input, Context context) {
	StringBuffer textualTransformedInput = this.performTextualTransformations(input, context);

	List<DSLDefinition> dsls = context.getDsls();

	GrammarBuilder grammarBuilder = new GrammarBuilder(ult);
	IGrammar<String> grammar = grammarBuilder.buildGrammarFromDefinitions(dsls);

	if (logger.isDebugEnabled()) {
	    logger.debug("Grammar successfully construced");
	    logger.trace("Grammar is: {}", grammar);
	}

	ATerm term = this.parseResource(textualTransformedInput, grammar);

	Map<String, MethodOptions> methodOptions = grammarBuilder.getMethodOptions();
	ATerm astTransformedTerm = this.performASTTransformations(term, context, methodOptions);

	ByteArrayOutputStream out = this.performPrettyPrinting(astTransformedTerm);
	return out;
    }

    protected ByteArrayOutputStream performPrettyPrinting(ATerm term) {

	try {
	    term.accept(prettyPrinter);
	} catch (VisitFailure e) {
	    logger.warn("Pretty printing on term failed", e);
	}

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	prettyPrinter.write(out);

	return out;
    }

    private StringBuffer performTextualTransformations(StringBuffer originalInput, Context context) {
	StringBuffer transformedInput = new StringBuffer(originalInput);
	logger.trace("starting textual transformations");
	ArrayList<TransformationType> idents = new ArrayList<TransformationType>(context.getDsls());
	idents.add(context.getFiletype());
	Collection<TextualTransformation> configuredTextualTransformers = getTransformerProvider()
		.getConfiguredTextualTransformers(idents.toArray(new TransformationType[0]));
	logger.trace("found transformations {}", configuredTextualTransformers);
	for (TextualTransformation t : configuredTextualTransformers) {
	    transformedInput = t.transform(context, transformedInput);
	}
	return transformedInput;
    }

    private ATerm performASTTransformations(ATerm aterm, Context context, Map<String, MethodOptions> methodOptions) {
	logger.trace("starting ast transformations");
	ArrayList<TransformationType> idents = new ArrayList<TransformationType>(context.getDsls());
	idents.add(context.getFiletype());
	Set<ASTTransformation> configuredTextualTransformers = getTransformerProvider().getConfiguredASTTransformers(
		idents.toArray(new TransformationType[0]));
	logger.trace("found transformations {}", configuredTextualTransformers);
	for (ASTTransformation t : configuredTextualTransformers) {
	    aterm = t.transform(methodOptions, aterm);
	}
	return aterm;
    }

    protected ATerm parseResource(StringBuffer input, IGrammar<String> grammar) {
	KeywordSensitiveLexer ksl = new KeywordSensitiveLexer(new KeywordSeperator());

	EarleyParser parser = new EarleyParser(ksl, grammar);
	IChart chart = parser.parse(input.toString().trim());

	IAbstractNode program = chart.getAST();
	ATermBuilder aterm = new ATermBuilder(program);

	ATerm term = aterm.getATerm();

	return term;
    }

    /**
     * @param resource
     *            the resource to read
     * @return The content of {@code resource} or <code>null</code> if resource
     *         can not be read
     */
    private @CheckForNull
    StringBuffer readResource(IFile resource) {
	try {
	    String stringFromReader = IOUtils.toString(resource.getContents());
	    return new StringBuffer(stringFromReader);
	} catch (IOException e) {
	    logger.error("Failed to read resource.", e);
	} catch (CoreException e) {
	    logger.error("Failed to obtain content of specified resource.", e);
	}
	return null;
    }

    private void writeResourceContent(ByteArrayOutputStream content, IFile file) {
	ByteArrayInputStream bais = new ByteArrayInputStream(content.toByteArray());
	try {
	    if (!file.exists()) {
		IFolder fileParentFolder = file.getProject().getFolder(
			file.getProjectRelativePath().removeLastSegments(1));
		createFolders(fileParentFolder);
		file.create(bais, IResource.FORCE | IResource.DERIVED, null);
	    } else {
		/*
		 * IResource.DERIVED is ignored in case of setContents
		 */
		file.setContents(bais, IResource.FORCE, null);
	    }
	} catch (CoreException e) {
	    logger.error("Failed to write to {}", file.getFullPath(), e);
	}
    }

    private void createFolders(IFolder folder) throws CoreException {

	if (!folder.exists()) {
	    IPath parentPath = folder.getProjectRelativePath().removeLastSegments(1);
	    boolean notProjectRoot = !parentPath.isEmpty();
	    if (notProjectRoot) {
		IProject project = folder.getProject();
		IFolder parentFolder = project.getFolder(parentPath);
		if (!parentFolder.exists()) {
		    createFolders(parentFolder);
		}
	    }
	    folder.create(false, true, null);
	}
    }

    protected Set<DSLDefinition> determineActiveInvolvedDSLs(IFile srcFile, StringBuffer resourceContent)
	    throws DSLNotFoundException {

	Set<String> determinedDSLNames = determineInvolvedDSLNames(srcFile, resourceContent);

	return getActiveDSLDefinitionsForNames(determinedDSLNames);
    }

    private Set<String> determineInvolvedDSLNames(IFile srcFile, StringBuffer resourceContent)
	    throws DSLNotFoundException {
	List<String> determinedDSLNames = ResourceHandlingHelper.determineInvolvedDSLNames(srcFile, resourceContent,
		getLanguageProvider(), fileType);
	return new HashSet<String>(determinedDSLNames);
    }

    private Set<DSLDefinition> getActiveDSLDefinitionsForNames(Set<String> determinedDSLNames) {
	Set<DSLDefinition> determinedDSLs = new HashSet<DSLDefinition>(determinedDSLNames.size());
	for (String dslName : determinedDSLNames) {
	    DSLDefinition activeDSL = getLanguageProvider().getActiveDSLForExtension(dslName);
	    if (activeDSL != null) {
		determinedDSLs.add(activeDSL);
	    }
	}
	return determinedDSLs;
    }

}