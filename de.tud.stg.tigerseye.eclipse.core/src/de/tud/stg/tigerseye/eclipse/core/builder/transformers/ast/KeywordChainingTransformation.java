package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import de.tud.stg.tigerseye.eclipse.core.api.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.RecursiveVisitor;

/**
 * {@link KeywordChainingTransformation} is capable of detecting and rebuilding
 * chained keywords into the callable methods of the DSL interface
 * 
 * @author Kamil Erhard
 * @author Leo_Roos
 * 
 */
public class KeywordChainingTransformation implements ASTTransformation {
private static final Logger logger = LoggerFactory.getLogger(KeywordChainingTransformation.class);


    // private final static ATerm methodCompositionTemplate = SingletonFactory
    // .getInstance().make("[<list>]");

	public KeywordChainingTransformation() {

	}

    @Override
    public ATerm transform(Map<String, DSLMethodDescription> moptions, ATerm aterm) {

	try {
	    aterm = (ATerm) aterm.accept(new KeywordChainingATermVisitor(moptions));
	} catch (VisitFailure e) {
	    logger.warn("Generated log statement", e);
	}

	return aterm;
    }



	public static void main(String[] args) {
		PureFactory fac = SingletonFactory.getInstance();

		ATerm term = fac.make("foo{[KEY, VALUE]}");
	System.out.println("term is {}" + term);
	System.out.println("annotations are {}" + term.getAnnotations());

		ATerm match = fac.make("[[<fun>, <fun>]]");
	System.out.println("match is {}" + match);

		System.out.println("annotationmatch is {}" + term.getAnnotations().match(match));
	}

	public String getName() {
		return "Keyword Chaining Transformation";
	}

	@Override
	public String getDescription() {
		return "This transformation allows the chaining of specified keywords into a method";
	}

	@Override
	public Set<TransformationType> getSupportedFileTypes() {
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

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.KEYWORD_CHAINING_TRANSFORMATION;
    }

    private static class KeywordChainingATermVisitor extends RecursiveVisitor {


	private enum Type {
	    LIST, APPL;
	}

	private final Map<String, DSLMethodDescription> moptions;

	public KeywordChainingATermVisitor(Map<String, DSLMethodDescription> moptions) {
	    this.moptions = moptions;
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

	private ATerm transformStatement(ATerm arg, ATerm annotation, Type type) {

	    String statement = ((ATermAppl) annotation).getName();

	    DSLMethodDescription methodOptions = moptions.get(statement);

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

    }
}
