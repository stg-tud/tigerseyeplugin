package de.tud.stg.popart.builder.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.IChart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.builder.core.GrammarBuilder;
import de.tud.stg.popart.builder.core.aterm.ATermBuilder;
import de.tud.stg.popart.builder.core.aterm.CodePrinter;
import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.AnnotationExtractor;
import de.tud.stg.popart.builder.transformers.Context;
import de.tud.stg.popart.builder.transformers.Filetype;
import de.tud.stg.popart.builder.transformers.TextualTransformation;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.eclipse.LanguageProviderImpl;
import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.ILanguageProvider;
import de.tud.stg.tigerseye.core.OutputPathHandler;
import de.tud.stg.tigerseye.core.TigerseyeCore;

public class DSLResourceHandler implements ResourceHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(DSLResourceHandler.class);

	private final String outputFileNameEnding;
	private final String srcFileNameEnding;

	private final CodePrinter prettyPrinter;

	public DSLResourceHandler(String fileExtension, String fileNameEnding,
			CodePrinter prettyPrinter) {
		outputFileNameEnding = fileExtension;
		srcFileNameEnding = fileNameEnding;
		this.prettyPrinter = prettyPrinter;
	}

	@Override
	public void handleResource(IResource resource) {
		logger.debug("handling resource {}", resource);

		StringBuffer input = this.readResource(resource);

		Context context;
		try {
			context = this.determineInvolvedDSLs(resource, input);
		} catch (DSLNotFoundException e) {
			logger.error("Resource {} could not be handled. {}", new Object[] {
					resource, e.noDSLMsg() }, e);
			return;
		}


		Filetype filetype = Filetype.getTypeForSrcResource(resource.getName());

		if (filetype == null) {
			logger.error("No filetype for {} could be determined",
					resource.getName());
			return;
		}

		context.setFiletype(filetype);
		input = this.performTextualTransformations(input, context);

		GrammarBuilder grammar = this.buildNeccessaryGrammar(context);
		context.setGrammarBuilder(grammar);

		ATerm term = this.parseResource(input, context);

		term = this.performASTTransformations(term, context);

		ByteArrayOutputStream out = this.performPrettyPrinting(term);

		IFile outputFile = new OutputPathHandler(filetype)
				.getProjectRelativeOutputFile(resource);
		this.writeResource(outputFile, out);
	}

	protected String getFileExtension() {
		return outputFileNameEnding;
	}

	protected GrammarBuilder buildNeccessaryGrammar(Context context) {
		GrammarBuilder gb = new GrammarBuilder();
		IGrammar<String> grammar = gb.buildGrammar(context.getDSLClasses());

		if (logger.isDebugEnabled()) {
			logger.debug("Grammar successfully construced");
			logger.trace("Grammar: {}", grammar.toString());
		}

		return gb;
	}

	protected Context determineInvolvedDSLs(IResource resource,
			StringBuffer input) throws DSLNotFoundException {
		Context context = new Context(resource.getName());

		int fileExtensionIndex = resource.getName().lastIndexOf(
				srcFileNameEnding);
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

				this.addDSLToContext(dslName, context);
			}
		} else {
			AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(
					EDSL.class);
			extractor.setInput(input.toString());

			EDSL annotation = extractor.find();
			edslAnnotations.add(extractor.getBounds());

			if (annotation == null) {
				return context;
			}

			for (String dslName : annotation.value()) {
				this.addDSLToContext(dslName, context);
			}
		}

		for (int[] b : edslAnnotations) {
			input.delete(b[0], b[1]);
		}

		return context;
	}

	private void addDSLToContext(String dslName, Context context)
			throws DSLNotFoundException {
		logger.debug("looking for dsl of extension: " + dslName);
		Class<? extends DSL> clazz = this.getDslClass(dslName);

		if (clazz != null) {
			context.addDSL(dslName, clazz);
			logger.debug("added dsl '{}' to context", dslName);
		} else {
			DSLNotFoundException e = new DSLNotFoundException();
			e.setDSL(dslName);
			throw e;
		}
	}

    protected Class<? extends DSL> getDslClass(String dslName)
	    throws DSLNotFoundException {
		if (dslName != null) {
			String className;
			String symbolicName;
			

			ILanguageProvider iLanguageProvider = new LanguageProviderImpl(TigerseyeCore.getPreferences());

	    List<DSLDefinition> dslList = iLanguageProvider
						.getDSLForExtension(dslName);
	    Iterator<DSLDefinition> iterator = dslList.iterator();
	    while (iterator.hasNext()) {
		DSLDefinition next = iterator.next();
		if (!next.isActive())
		    iterator.remove();
	    }
	    if (dslList.size() != 1) {

		logger.error(
			"Found {} DSLdefinitions. Only exactly one active dsl for one extension is a valid configuration. DSLs where {}.",
			dslList.size(), dslList);
		throw new DSLNotFoundException(
			"Invalid number of DSLs configured: " + dslList.size())
			.setDSL(dslName);
	    }

	    DSLDefinition dslDef = dslList.get(0);
				className = dslDef.getClassPath();
				symbolicName = dslDef.getContributorSymbolicName();
				logger.debug(
						"Found configuration for dsl \"{}\" with className \"{}\" and symbolicName \"{}\"",
						new Object[] { dslName, className, symbolicName });

			try {
				Class<?> loadedDSL = Platform.getBundle(symbolicName)
						.loadClass(className);
				@SuppressWarnings("unchecked")
				Class<? extends DSL> dsl = (Class<? extends DSL>) loadedDSL;
				return dsl;
			} catch (ClassNotFoundException e) {
				logger.warn("class not found in bundle " + symbolicName
						+ " for name " + className, e);
			} catch (ClassCastException e) {
				logger.error(
						"class {} was not of type {}. Cannot process dsl {}",
						new Object[] { className, DSL.class.getName(), dslName });
			}
		}
		return null;
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

	protected StringBuffer performTextualTransformations(StringBuffer input,
			Context context) {
		input = new StringBuffer(input);
		logger.trace("starting textual transformations");
	DSLBuilderActivator plugin = new DSLBuilderActivator();
		Collection<TextualTransformation> textualTransformers = plugin
				.getConfiguredTextualTransformers(context.getFiletype(),
						context.getDSLExtensions());
		logger.trace("Found textual transformers: {}",
				textualTransformers.toArray());
		for (TextualTransformation t : textualTransformers) {
			input = t.transform(context, input);
		}
		return input;
	}

	protected ATerm performASTTransformations(ATerm aterm, Context context) {
		logger.trace("starting ast transformations");
	for (ASTTransformation t : new DSLBuilderActivator()
				.getConfiguredASTTransformers(context.getFiletype(),
						context.getDSLExtensions())) {
			aterm = t.transform(context, aterm);
		}
		return aterm;
	}

	protected ATerm parseResource(StringBuffer input, Context context) {
		KeywordSensitiveLexer ksl = new KeywordSensitiveLexer(
				new KeywordSeperator());

		EarleyParser parser = new EarleyParser(ksl, context.getGrammarBuilder()
				.getGrammar());
		IChart chart = parser.parse(input.toString().trim());

		IAbstractNode program = chart.getAST();
		ATermBuilder aterm = new ATermBuilder(program);

		ATerm term = aterm.getATerm();

		return term;
	}

	protected StringBuffer readResource(IResource resource) {

		if (!(IResource.FILE == resource.getType())) {
			throw new IllegalArgumentException(
					"Only file resources are processed, but was of IResource integer: "
							+ resource.getType());
		}
		StringBuffer buffer = new StringBuffer();
		try {
			IPath path = resource.getProjectRelativePath();
			IFile file = resource.getProject().getFile(path);
			String stringFromReader = IOUtils.toString(file.getContents());
			buffer = new StringBuffer(stringFromReader);
		} catch (IOException e) {
			logger.error("Failed to read resource.", e);
		} catch (CoreException e) {
			logger.error("Failed to obtain content of specified resource.", e);
		}
		return buffer;
	}

	protected void writeResource(IFile file, ByteArrayOutputStream out) {
		ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
		try {
			NullProgressMonitor monitor = new NullProgressMonitor();
			if (!file.exists()) {
				File output = file.getRawLocation().toFile();
				File dir = new File(output.getParent());
		boolean mkdirs = dir.mkdirs();

		logger.debug("dir: " + dir + ", mkdirs: ");
				file.create(bais, IResource.FORCE | IResource.DERIVED, monitor);
			} else {
				/*
				 * IResource.DERIVED is ignored in case of setContents
				 */
				file.setContents(bais, IResource.FORCE, monitor);
			}
		} catch (CoreException e) {
			logger.error(
					"Faild to write transformed resource to output location.",
					e);
		}
	}
}
