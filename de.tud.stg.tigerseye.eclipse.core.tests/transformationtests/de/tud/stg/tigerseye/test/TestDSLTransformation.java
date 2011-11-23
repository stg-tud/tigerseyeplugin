package de.tud.stg.tigerseye.test;

import static de.tud.stg.tigerseye.test.TransformationUtils.dslSingle;
import static de.tud.stg.tigerseye.test.TransformationUtils.newGrammar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import jjtraveler.VisitFailure;

import org.apache.bsf.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.TestUtils;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.ILexer;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.EarleyParserConfiguration;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherGroovyTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;

public class TestDSLTransformation {

	private static final Logger logger = LoggerFactory.getLogger(TestDSLTransformation.class);

	private final UnicodeLookupTable ult;
	private CodePrinterFactory cpf;

	public TestDSLTransformation(UnicodeLookupTable ult, CodePrinterFactory cpf) {
		this.ult = ult;
		this.cpf = cpf;
	}

	public TestDSLTransformation(CodePrinterFactory cpf) throws FileNotFoundException {
		this(TransformationUtils.getDefaultLookupTable(), cpf);
	}

	public TestDSLTransformation() throws FileNotFoundException {
		this(TransformationUtils.getDefaultLookupTable(), new PrettyGroovyCodePrinterFactory());
	}

	public String performTransformation(InputStream inputStream, List<Class<? extends DSL>> classes)
			throws IOException, VisitFailure, FileNotFoundException {
		String sb = IOUtils.getStringFromReader(new InputStreamReader(inputStream));

		return performTransformation(sb, classes);
	}
	
	public String performTransformation(String sb, Class<? extends DSL> clazz) throws VisitFailure {
		return performTransformation(sb, TransformationUtils.dslSingle(clazz));
	}

	public String performTransformation(String sb, List<Class<? extends DSL>> classes) throws VisitFailure {
		GrammarBuilder gb = new GrammarBuilder(ult);

		IGrammar<String> grammar = gb.buildGrammar(classes);

		String performTransformation = performTransformation(sb, new GrammarResult(grammar, gb.getMethodOptions(),
				classes));

		return performTransformation;
	}

	public static class GrammarResult {

		public Map<String, DSLMethodDescription> moptions;
		public IGrammar<String> grammar;
		public final List<Class<? extends DSL>> classes;

		public GrammarResult(IGrammar<String> buildGrammar, Map<String, DSLMethodDescription> methodOptions,
				Class<? extends DSL>... cs) {
			grammar = buildGrammar;
			moptions = methodOptions;
			classes = (List<Class<? extends DSL>>) Arrays.asList(cs);
		}

		public GrammarResult(IGrammar<String> buildGrammar, Map<String, DSLMethodDescription> methodOptions,
				List<Class<? extends DSL>> cs) {
			grammar = buildGrammar;
			moptions = methodOptions;
			classes = cs;
		}

		@Deprecated
		public Context generateContext(@Nullable String contextName) {
			if (contextName == null)
				contextName = "no_context_name_given";
			Context context = new Context(contextName);
			for (int i = 0; i < classes.size(); i++) {
				context.addDSL("anyextension" + i, (Class<? extends DSL>) classes.get(i));
			}
			context.setFiletype(null);
			return context;
		}

	}

	/**
	 * Only performs Keyword transformation no additional wrapping into Closures or DSLInvokers or adding Packages etc.
	 */
	public String performTransformation(String sb, GrammarResult gr) throws VisitFailure {

		EarleyParser earleyParser = new EarleyParserConfiguration().getDefaultEarleyParserConfiguration(gr.grammar);

		Chart chart = (Chart) earleyParser.parse(sb.trim());
		logger.debug("Resulting AST for classes {} is:\n{}", gr.classes, chart.getAST());

		// int cnt = 0;
		// do {
		IAbstractNode ast = chart.getAST();
		// chart.nextAmbiguity();
		// cnt++;
		// logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		Map<String, DSLMethodDescription> moptions = gr.moptions;

		Context context = new Context("dummy");
		context.setDSLMethodDescriptions(moptions);
		
		term = new KeywordChainingTransformation().transform(context, term);

		return aTermToString(term, this.cpf.createCodePrinter());
	}

	public static String performCustomTransformation(Class<? extends DSL> clazz, String input, CodePrinter cp, ASTTransformation ... astts) throws VisitFailure {
		return performCustomTransformation(dslSingle(clazz), input, cp, astts);
	}

	public static String performCustomTransformation(List<Class<? extends DSL>> clazzes, String input, CodePrinter codePrinter, ASTTransformation ... astts) throws VisitFailure {
	
		GrammarResult gr = newGrammar(clazzes);
	
		IAbstractNode ast = getAST(input.trim(), gr.grammar);
	
		ATerm term = new ATermBuilder(ast).getATerm();
	

		Context c = new Context("dummy");
		c.setDSLMethodDescriptions(gr.moptions);
		
		// performs keyword to methods transformation
		term = new KeywordChainingTransformation().transform(c, term);
	
		
		// encloses DSL method calls into invocations
		for (ASTTransformation astTransformation : astts) {			
			term = astTransformation.transform(c, term);
		}
	
		String output = aTermToString(term, codePrinter);
		return output;
	}

	private static IAbstractNode getAST(String input, IGrammar<String> grammar) {
		EarleyParser earleyParser = new EarleyParserConfiguration().getDefaultEarleyParserConfiguration(grammar);
		Chart chart = (Chart) earleyParser.parse(input);
		IAbstractNode ast = chart.getAST();
		return ast;
	}

	private static String aTermToString(ATerm term, CodePrinter prettyPrinter) throws VisitFailure {
		term.accept(prettyPrinter);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prettyPrinter.write(out);

		return new String(out.toByteArray());
	}

}
