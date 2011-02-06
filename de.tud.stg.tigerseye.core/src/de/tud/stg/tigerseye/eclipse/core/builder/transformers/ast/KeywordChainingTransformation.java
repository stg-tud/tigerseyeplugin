package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jjtraveler.VisitFailure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import aterm.Visitable;
import aterm.pure.PureFactory;
import aterm.pure.SingletonFactory;
import de.tud.stg.popart.builder.core.GrammarBuilder;
import de.tud.stg.popart.builder.core.GrammarBuilder.MethodOptions;
import de.tud.stg.popart.builder.core.aterm.RecursiveVisitor;
import de.tud.stg.popart.builder.transformers.ASTTransformation;
import de.tud.stg.popart.builder.transformers.Context;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.TransformationUtils;

/**
 * {@link KeywordChainingTransformation} is capable of detecting and rebuilding chained keywords into the callable
 * methods of the dsl interface
 *
 * @author Kamil Erhard
 *
 */
public class KeywordChainingTransformation extends RecursiveVisitor implements ASTTransformation {
private static final Logger logger = LoggerFactory.getLogger(KeywordChainingTransformation.class);


	private GrammarBuilder gb;
	private final static ATerm methodCompositionTemplate = SingletonFactory
			.getInstance().make("[<list>]");

	public KeywordChainingTransformation() {

	}

	public KeywordChainingTransformation(Context context) {
		gb = context.getGrammarBuilder();
	}

	private ATerm checkMethodComposition(ATerm arg, Type type) {

		ATerm v0 = arg.getAnnotation(factory.make("LHS"));

		if (v0 != null) {
			ATerm v1 = arg.getAnnotation(factory.make("RHS"));

			if (v1 != null) {
				return this.transformStatement(arg, v1, type);
			}
		}

		return arg;
	}

	private enum Type {
		LIST, APPL;
	}

	private ATerm transformStatement(ATerm arg, ATerm annotation, Type type) {

		String statement = ((ATermAppl) annotation).getName();

		MethodOptions methodOptions = gb.getMethodOptions(statement);

		boolean isComposedMethod = methodOptions != null;

		if (!isComposedMethod) {
			return arg;
		}

		if (type == Type.LIST) {
			arg = (ATerm) arg.getChildAt(0);
		}

		List<Integer> paramaterIndices = methodOptions.getParamaterIndices();

		int parameterCount = paramaterIndices.size();

		ATerm[] terms = new ATerm[paramaterIndices.size()];

		for (int i = 0; i < paramaterIndices.size(); i++) {
			terms[i] = ((ATermList) arg).elementAt(paramaterIndices.get(i));
		}

		String fun = methodOptions.getMethodCallName();
		ATermAppl appl = factory.makeAppl(factory.makeAFun(fun, parameterCount, false), terms);
		appl = (ATermAppl) appl.setAnnotations(arg.getAnnotations());

		return appl;
	}

	@Override
	public ATerm transform(Context context, ATerm aterm) {

		KeywordChainingTransformation keywordChainingTransformer = new KeywordChainingTransformation(context);

		try {
			aterm = (ATerm) aterm.accept(keywordChainingTransformer);
		} catch (VisitFailure e) {
			logger.warn("Generated log statement",e);
		}

		return aterm;
	}

	@Override
	public Visitable visitList(ATermList arg) throws VisitFailure {

		arg = (ATermList) super.visitList(arg);
		ATerm v = this.checkMethodComposition(arg, Type.LIST);
		return v;
	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {

		arg = (ATermAppl) super.visitAppl(arg);
		ATerm v = this.checkMethodComposition(arg, Type.APPL);
		return v;
	}

	public static void main(String[] args) {
		PureFactory fac = SingletonFactory.getInstance();

		ATerm term = fac.make("foo{[KEY, VALUE]}");
		logger.info("term is {}", term);
		logger.info("annotations are {}", term.getAnnotations());

		ATerm match = fac.make("[[<fun>, <fun>]]");
		logger.info("match is {}", match);

		logger.info("annotationmatch is {}", term.getAnnotations().match(match));
	}

	public String getName() {
		return "Keyword Chaining Transformation";
	}

	@Override
	public String getDescription() {
		return "This transformation allows the chaining of specified keywords into a method";
	}

	@Override
	public Set<FileType> getSupportedFileTypes() {
		return TransformationUtils.getSetForFiletypes(FileType.TIGERSEYE,
				FileType.JAVA, FileType.GROOVY);
	}

	@Override
	public Set<ATerm> getRequirements() {
		return Collections.emptySet();
	}

	@Override
	public Set<ATerm> getAssurances() {
		return Collections.emptySet();
	}
}
