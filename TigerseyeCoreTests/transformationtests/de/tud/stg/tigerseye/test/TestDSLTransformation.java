package de.tud.stg.tigerseye.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import jjtraveler.VisitFailure;

import org.apache.bsf.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.ILexer;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.MethodOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;

public class TestDSLTransformation {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(TestDSLTransformation.class);

	private final UnicodeLookupTable ult;
	private CodePrinterFactory cpf;

	public TestDSLTransformation(UnicodeLookupTable ult, CodePrinterFactory cpf) {
		this.ult = ult;
		this.cpf = cpf;
	}

	public TestDSLTransformation(CodePrinterFactory cpf)
			throws FileNotFoundException {
		this.ult = TestUtils.getDefaultLookupTable();
		this.cpf = cpf;
	}

	public String performTransformation(InputStream inputStream,
			Class<? extends DSL>[] classes) throws IOException, VisitFailure,
			FileNotFoundException {
		String sb = IOUtils.getStringFromReader(new InputStreamReader(
				inputStream));

		return performTransformation(sb, classes);
	}

	public String performTransformation(String sb,
			Class<? extends DSL>[] classes) throws VisitFailure {
		GrammarBuilder gb = new GrammarBuilder(ult);
		IGrammar<String> grammar = gb.buildGrammar(classes);

		String performTransformation = performTransformation(sb,
				new GrammarResult(grammar, gb.getMethodOptions(), classes));

		return performTransformation;
	}

	public static class GrammarResult {

		public Map<String, MethodOptions> moptions;
		public IGrammar<String> grammar;
		public final Class<?>[] classes;

		public GrammarResult(IGrammar<String> buildGrammar,
				Map<String, MethodOptions> methodOptions, Class<?>... classes) {
			grammar = buildGrammar;
			moptions = methodOptions;
			this.classes = classes;
		}

	}

	public String performTransformation(String sb, GrammarResult gr)
			throws VisitFailure {

		ILexer lexer = new KeywordSensitiveLexer(new KeywordSeperator());
		EarleyParser earleyParser = new EarleyParser(lexer, gr.grammar);

		
		// logger.info("= Parsing input stream = {}", inputStream);

		Chart chart = (Chart) earleyParser.parse(sb.trim());
		logger.debug("Resulting AST is:\n{}", chart.getAST());

		Context context = new Context("dummyFileName");
		for (Class<?> clazz : gr.classes) {
			context.addDSL(clazz.getSimpleName(), (Class<? extends DSL>) clazz);
		}

		// int cnt = 0;
		// do {
		IAbstractNode ast = chart.getAST();
		// chart.nextAmbiguity();
		// cnt++;
		// logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		Map<String, MethodOptions> moptions = gr.moptions;

		term = new KeywordChainingTransformation().transform(moptions, term);

		if (gr.classes.length > 1) {
			// term = new ClosureResultTransformer().transform(context,
			// term);
			term = new InvokationDispatcherTransformation().transform(moptions,
					term);
		}
		CodePrinter prettyPrinter = this.cpf.createCodePrinter();

		term.accept(prettyPrinter);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prettyPrinter.write(out);

		return new String(out.toByteArray());
	}

}
