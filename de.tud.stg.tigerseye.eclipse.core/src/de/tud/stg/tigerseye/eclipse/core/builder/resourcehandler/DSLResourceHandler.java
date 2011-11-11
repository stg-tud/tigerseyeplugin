/**
 *
 */
package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jjtraveler.VisitFailure;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
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
import de.tud.stg.tigerseye.eclipse.core.utils.InvolvedDSLsExtractor;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;

public abstract class DSLResourceHandler implements IResourceDeltaVisitor {

    public static final String TRANSFORMATION_DEBUG_SYSTEM_PROPERTY = "tigerseye.transformation.debug";

    private static final Logger logger = LoggerFactory.getLogger(DSLResourceHandler.class);

    private final CodePrinter prettyPrinter;

    private TransformerConfigurationProvider transformerProvider;

    private OutputPathHandler outputPathHandler;

    private UnicodeLookupTable ult;

    public final FileType fileType;

    private static final Map<IFile, Long> lastTimeHandledCache = new Hashtable<IFile, Long>();

    // XXX(Leo_Roos;Nov 9, 2011) dirty fix for more debug information about
    // transformation process for every built file. Could be turned into
    // preference.
    private boolean tigerseyetransforamtiondebug = false;

    @Override
    public boolean visit(IResourceDelta delta) {

	// FIXME(leo;10.11.2011) QOD to suppress build of files copied into
	// bin folder
	IResource aResource = delta.getResource();
	if (aResource.getFullPath().toString().contains("/bin/"))
	    return false;

	IFile file;
	if (aResource instanceof IFile) {
	    if (!isInteresstedIn(aResource))
		return false;

	    file = (IFile) aResource;
	    if (fileNeedsRehandle(file)) {
		rehandleFile(delta, file);
	    } else {
		logger.info("Already handled {}. Skipping file.", file);
	    }
	    return false;
	} else {
	    logger.trace("Skipping resource {}, since not of type IFile", aResource);
	    // Returning true since this will usually mean the resource is a
	    // folder that might contain files that are of interest to me, i.e.
	    // .dsl Files
	    return true;
	}

    }

    private void rehandleFile(IResourceDelta delta, IFile file) {
	int kind = delta.getKind();
	switch (kind) {
	case IResourceDelta.CHANGED:
	    logger.trace("File '{}' changed.", file);
	    boolean wasHandled = handleChanged(file);
	    if (!wasHandled) {
		logger.debug("Failed to handle file {} ", file);
	    }
	    break;
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
    }

    private boolean fileNeedsRehandle(IFile file) {
	boolean fileHasChanged = fileHasChanged(file);
	if (fileHasChanged)
	    return true;
	else {
	    boolean exists = this.outputPathHandler.getOutputFile(file).exists();
	    return !exists;
	}
    }

    private boolean fileHasChanged(IFile file) {
	long modificationStamp = file.getModificationStamp();
	if (modificationStamp < 0) {
	    logger.warn("file not accessible or has no valid modification timestamp. File: {} Timestamp: {}", file,
		    modificationStamp);
	    return false;
	}

	Long lastModificationStamp = lastTimeHandledCache.get(file);

	if (lastModificationStamp != null && lastModificationStamp == modificationStamp) {
	    return false;
	} else {
	    lastTimeHandledCache.put(file, modificationStamp);
	    return true;
	}
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

    /*
     * return true if file could be handled, all DSLs have been active, no
     * exception was thrown
     */
    private boolean handleChanged(IFile file) {

	boolean canHandleDelta = allDSLsForDeltaAreActive(file);
	if (!canHandleDelta)
	    return false;

	try {
	    if (this.isInteresstedIn(file)) {
		handleResource(file);
		return true;
	    }
	} catch (Exception e) {
	    logger.error("failed vissiting file {}", file, e);
	}
	return false;
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

	String property = System.getProperty(TRANSFORMATION_DEBUG_SYSTEM_PROPERTY);
	if (property != null)
	    this.tigerseyetransforamtiondebug = true;
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
	Set<DSLDefinition> dslDefinitions = null;
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
	if (dslDefinitions == null || dslDefinitions.size() < 1) {
	    // XXX(Leo_Roos;Nov 11, 2011) Might be still valid to just output
	    // file without changes
	    logger.trace("No DSLs for {} determined. Will not attempt a transformation.", resource);
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
	String workingInput = textualTransformedInput.toString().trim();

	List<DSLDefinition> dsls = context.getDsls();

	GrammarBuilder grammarBuilder = new GrammarBuilder(ult);
	IGrammar<String> grammar = grammarBuilder.buildGrammarFromDefinitions(dsls);

	if (logger.isDebugEnabled()) {
	    logger.debug("Grammar successfully construced");
	    logger.trace("Grammar is: {}", grammar);
	}

	EarleyParser parser = new EarleyParserConfiguration().getDefaultEarleyParserConfiguration(grammar);

	IChart chart = parser.parse(workingInput);

	ATerm term = getATermFromChart(chart);

	Map<String, MethodOptions> methodOptions = grammarBuilder.getMethodOptions();

	if (tigerseyetransforamtiondebug) {
	    // FIXME(Leo_Roos;Nov 9, 2011) unflexible solution
	    IFile transformedFile = context.getTransformedFile();
	    IPath projectRelativePath = transformedFile.getProjectRelativePath();
	    String fileName = projectRelativePath.lastSegment();
	    IPath srcRelativePath = projectRelativePath.removeFirstSegments(1).removeLastSegments(1);
	    IPath debugFileCoreName = new Path("debugtigerseye").append(srcRelativePath);
	    IProject project = transformedFile.getProject();

	    IFile chartASTFile = project.getFile(debugFileCoreName.append(fileName + ".chartAST"));
	    writeResourceContent(chart.getAST().toString(), chartASTFile);

	    IFile grammarFile = project.getFile(debugFileCoreName.append(fileName + ".grammar"));
	    writeResourceContent(grammar.toString(), grammarFile);

	}

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
		.getConfiguredTextualTransformers(idents.toArray(new TransformationType[idents.size()]));
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
		idents.toArray(new TransformationType[idents.size()]));
	logger.trace("found transformations {}", configuredTextualTransformers);
	for (ASTTransformation t : configuredTextualTransformers) {
	    aterm = t.transform(methodOptions, aterm);
	}
	return aterm;
    }

    private ATerm getATermFromChart(IChart chart) {
	IAbstractNode program = chart.getAST();

	ATermBuilder aterm = new ATermBuilder(program);

	ATerm term = aterm.getATerm();

	return term;
    }

    private void writeResourceContent(ByteArrayOutputStream content, IFile file) {
	ByteArrayInputStream bais = new ByteArrayInputStream(content.toByteArray());
	writeResourceContent(file, bais);
    }

    private void writeResourceContent(String content, IFile file) {
	ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
	writeResourceContent(file, bais);
    }

    private void writeResourceContent(IFile file, InputStream bais) {
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
	String resourceName = srcFile.getName();

	FileType resourceType = FileTypeHelper.getTypeForSrcResource(resourceName);

	if (!fileType.equals(resourceType)) {
	    logger.debug("resource {} of no interest for caller interested in {}", srcFile, fileType);
	    return Collections.emptySet();
	}

	return new InvolvedDSLsExtractor().determineInvolvedDSLNames(srcFile, resourceContent);
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