package de.tud.stg.tigerseye.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

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
	
	public TestDSLTransformation(CodePrinterFactory cpf) throws FileNotFoundException {
		this.ult = TestUtils.getDefaultLookupTable();
		this.cpf = cpf;	
	}

	public String performTransformation(InputStream inputStream,
			Class<? extends DSL>[] classes ) throws IOException, VisitFailure,
			FileNotFoundException {
		String sb = IOUtils.getStringFromReader(new InputStreamReader(inputStream));
		
		return performTransformation(sb,classes);
	}

	public String performTransformation(String sb, Class<? extends DSL>[] classes) throws VisitFailure {
		GrammarBuilder gb = new GrammarBuilder(ult);
		IGrammar<String> grammar = gb.buildGrammar(classes);

//		logger.info("= Grammar for classes: {}",Arrays.toString(classes));

		ILexer lexer = new KeywordSensitiveLexer(new KeywordSeperator());
		EarleyParser earleyParser = new EarleyParser(lexer, grammar);
		
//		logger.info("= Parsing input stream = {}", inputStream);			

		Chart chart = (Chart) earleyParser.parse(sb.trim());

		Context context = new Context("dummyFileName");
		context.setGrammarBuilder(gb);
		for (Class<? extends DSL> clazz : classes) {
			context.addDSL(clazz.getSimpleName(), clazz);
		}

//		int cnt = 0;
		// do {
		IAbstractNode ast = chart.getAST();
//		chart.nextAmbiguity();
//		cnt++;
//		logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		term = new KeywordChainingTransformation().transform(context, term);

		if (classes.length > 1) {
			// term = new ClosureResultTransformer().transform(context,
			// term);
			term = new InvokationDispatcherTransformation().transform(
					context, term);
		}
		CodePrinter prettyPrinter = this.cpf.createCodePrinter();
		
		term.accept(prettyPrinter);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prettyPrinter.write(out);

		return new String(out.toByteArray());
	}
	
}
