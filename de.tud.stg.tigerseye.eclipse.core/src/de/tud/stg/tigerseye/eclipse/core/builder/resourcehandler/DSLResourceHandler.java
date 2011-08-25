package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import de.tud.stg.popart.builder.eclipse.EDSL;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
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

public class DSLResourceHandler implements ResourceHandler {
    private static final Logger logger = LoggerFactory
	    .getLogger(DSLResourceHandler.class);

    private final CodePrinter prettyPrinter;

    private final FileType fileType;

    private ILanguageProvider languageProvider;

    private TransformerConfigurationProvider transformerProvider;

    private OutputPathHandler outputPathHandler;

    private UnicodeLookupTable ult;

    // private IPreferenceStore tigerseyePreferenceStore;

    public DSLResourceHandler(FileType fileType, CodePrinter prettyPrinter) {

	this.fileType = fileType;
	this.prettyPrinter = prettyPrinter;
	init();
    }

    private void init() {
	this.languageProvider = TigerseyeCore.getLanguageProvider();
	this.ult = TigerseyeCore.getUnicodeLookupTable();
	this.transformerProvider = new TransformerConfigurationProvider(
		TigerseyeCore.getTransformationProvider());
	this.outputPathHandler = new OutputPathHandler();
	// this.tigerseyePreferenceStore = TigerseyeCore.getPreferences();
    }

    protected ILanguageProvider getLanguageProvider() {
	return languageProvider;
    }

    protected TransformerConfigurationProvider getTransformerProvider() {
	return transformerProvider;
    }

    protected OutputPathHandler getOutputPathHandler() {
	return outputPathHandler;
    }

    @Override
    public void handleResource(IResource resource) {
	logger.debug("handling resource {}", resource);
	if (!(resource instanceof IFile)) {
	    logger.debug("Skipping resource {}, since not of type IFile",
		    resource);
	    return;
	}
	IFile srcFile = (IFile) resource;
	FileType filetype = FileTypeHelper.getTypeForSrcResource(srcFile
		.getName());
	if (filetype == null) {
	    logger.error("No filetype could be determined for {}",
		    srcFile.getName());
	    return;
	}
	IFile outputFile = getOutputPathHandler().getOutputFile(srcFile);
	if (outputFile == null) {
	    logger.error("Can not determine output file for {}", srcFile);
	    return;
	}
	StringBuffer resourceContent = this.readResource(srcFile);
	if (resourceContent == null) {
	    logger.error("Skipping unhandled resource {}", srcFile);
	    return;
	}
	Set<DSLDefinition> dslDefinitions = Collections.emptySet();
	try {
	    dslDefinitions = this.determineInvolvedDSLs(srcFile,
		    resourceContent);
	} catch (DSLNotFoundException e) {
	    logger.debug("Resource {} could not be handled. {}", new Object[] {
		    srcFile, e.noDSLMsg() }, e);
	}
	if (dslDefinitions.size() < 1) {
	    logger.trace("No DSLs for {} determined. Will not attempt a transformation.");
	    return;
	}
	Context context = new Context(resource.getName());
	context.addDSLs(dslDefinitions);
	context.setFiletype(filetype);
	ByteArrayOutputStream transformedContent = getTransformedContent(
		resourceContent, context);
	if (transformedContent.size() > 0) {
	    this.writeResourceContent(transformedContent, outputFile);
	} else {
	    logger.trace(
		    "Transformation for {} was empty. Will not write any change to file.",
		    resource);
	}

    }

    private ByteArrayOutputStream getTransformedContent(StringBuffer input,
	    Context context) {
	StringBuffer textualTransformedInput = this
		.performTextualTransformations(input, context);

	List<DSLDefinition> dsls = context.getDsls();

	GrammarBuilder grammarBuilder = new GrammarBuilder(ult);
	IGrammar<String> grammar = grammarBuilder.buildGrammarFromDefinitions(dsls);

	if (logger.isDebugEnabled()) {
	    logger.debug("Grammar successfully construced");
	    logger.trace("Grammar is: {}", grammar);
	}

	ATerm term = this.parseResource(textualTransformedInput, grammar);

	Map<String, MethodOptions> methodOptions = grammarBuilder
		.getMethodOptions();
	ATerm astTransformedTerm = this.performASTTransformations(term,
		context, methodOptions);

	ByteArrayOutputStream out = this
		.performPrettyPrinting(astTransformedTerm);
	return out;
    }


    /**
     * Will perform transformations on the passed input
     * 
     * @param resource
     * @param input
     * @return
     * @throws DSLNotFoundException
     */
    protected Set<DSLDefinition> determineInvolvedDSLs(IFile resource,
	    StringBuffer input)
	    throws DSLNotFoundException {
	Set<DSLDefinition> determinedDSLs = new HashSet<DSLDefinition>();
	ILanguageProvider languageProvider = getLanguageProvider();

	int fileExtensionIndex = resource.getName().lastIndexOf(
		fileType.srcFileEnding);
	if (fileExtensionIndex < 1) {
	    throw new DSLNotFoundException(
		    "No dsl extension could be determined for " + resource);
	}

	String[] str = resource.getName().substring(0, fileExtensionIndex - 1)
		.split("\\.");

	List<int[]> edslAnnotations = new LinkedList<int[]>();

	if (str.length > 1) {
	    for (int i = 1; i < str.length; i++) {
		String dslName = str[i];

		DSLDefinition activeDSLForExtension = languageProvider
			.getActiveDSLForExtension(dslName);
		if (activeDSLForExtension != null) {
		    try {
			this.addDSLToContext(activeDSLForExtension,
				determinedDSLs);
		    } catch (NoLegalPropertyFoundException e) {
			throw new DSLNotFoundException(e);
		    }
		}
	    }
	} else {

	    // Java EDSL Annotation, can be used to determine DSLs in Java file
	    // context
	    AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(
		    EDSL.class);
	    extractor.setInput(input.toString());

	    EDSL annotation = extractor.find();
	    edslAnnotations.add(extractor.getBounds());

	    if (annotation == null) {
		return determinedDSLs;
	    }

	    for (String dslName : annotation.value()) {
		DSLDefinition activeDSL = languageProvider
			.getActiveDSLForExtension(dslName);
		if (activeDSL != null) {
		    try {
			this.addDSLToContext(activeDSL, determinedDSLs);
		    } catch (NoLegalPropertyFoundException e) {
			throw new DSLNotFoundException(e);
		    }
		}
	    }
	}

	// Removes the EDSL annotation from the source file
	for (int[] b : edslAnnotations) {
	    input.delete(b[0], b[1]);
	}

	return determinedDSLs;
    }

    private void addDSLToContext(@Nonnull DSLDefinition clazz,
	    Set<DSLDefinition> context)
	    throws NoLegalPropertyFoundException {
	context.add(clazz);
	logger.trace("added dsl '{}' to context",
		clazz.getValue(DSLKey.EXTENSION));
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

    private StringBuffer performTextualTransformations(
	    StringBuffer originalInput, Context context) {
	StringBuffer transformedInput = new StringBuffer(originalInput);
	logger.trace("starting textual transformations");
	ArrayList<TransformationType> idents = new ArrayList<TransformationType>(
		context.getDsls());
	idents.add(context.getFiletype());
	Collection<TextualTransformation> configuredTextualTransformers = getTransformerProvider()
		.getConfiguredTextualTransformers(
			idents.toArray(new TransformationType[0]));
	logger.trace("found transformations {}", configuredTextualTransformers);
	for (TextualTransformation t : configuredTextualTransformers) {
	    transformedInput = t.transform(context, transformedInput);
	}
	return transformedInput;
    }

    private ATerm performASTTransformations(ATerm aterm, Context context,
	    Map<String, MethodOptions> methodOptions) {
	logger.trace("starting ast transformations");
	ArrayList<TransformationType> idents = new ArrayList<TransformationType>(
		context.getDsls());
	idents.add(context.getFiletype());
	Set<ASTTransformation> configuredTextualTransformers = getTransformerProvider()
		.getConfiguredASTTransformers(
			idents.toArray(new TransformationType[0]));
	logger.trace("found transformations {}", configuredTextualTransformers);
	for (ASTTransformation t : configuredTextualTransformers) {
	    aterm = t.transform(methodOptions, aterm);
	}
	return aterm;
    }

    protected ATerm parseResource(StringBuffer input, IGrammar<String> grammar) {
	KeywordSensitiveLexer ksl = new KeywordSensitiveLexer(
		new KeywordSeperator());

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
	ByteArrayInputStream bais = new ByteArrayInputStream(
		content.toByteArray());
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
	    IPath parentPath = folder.getProjectRelativePath()
		    .removeLastSegments(1);
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
}
