package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import jjtraveler.VisitFailure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import aterm.Visitable;
import aterm.pure.PureFactory;
import aterm.pure.SingletonFactory;
import de.tud.stg.tigerseye.dslsupport.DSLInvoker;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationUtils;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.RecursiveVisitor;

/**
 * {@link InvokationDispatcherGroovyTransformation} scans the AST for method calls to
 * a DSL interface and binds them to a instance of the concrete DSL by
 * prepending DSLInvoker.getDSL(DSL_CLASS). This supports multiple DSLs in one
 * AST.
 *
 * @author Kamil Erhard
 *
 */
public class InvokationDispatcherGroovyTransformation extends RecursiveVisitor implements ASTTransformation {
private static final Logger logger = LoggerFactory.getLogger(InvokationDispatcherGroovyTransformation.class);


	private final static AFun dslInvokerFunction;
    private Map<String, DSLMethodDescription> moptions;

	static {
		PureFactory factory = SingletonFactory.getInstance();
	String dslInvokerName = DSLInvoker.class.getSimpleName();
	dslInvokerFunction = factory.makeAFun(dslInvokerName + ".eval", 2, false);
	}

	public InvokationDispatcherGroovyTransformation() {
	}

    public InvokationDispatcherGroovyTransformation(
	    Map<String, DSLMethodDescription> moptions) {
	this.moptions = moptions;
	}

	public boolean areRequirementsSatisfied(Set<Class<? extends Transformation>> set) {
		return true;
	}

	public void ensures(Map<String, String> map) {

	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {
	// if (arg.getArity() > 0) {
			arg = (ATermAppl) super.visitAppl(arg);
			return this.transformATerm(arg);
	// } else {
	// return arg;
	// }
	}

    private Visitable transformATerm(ATermAppl arg) {

	ATerm v0 = arg.getAnnotation(factory.make("LHS"));

	if (v0 != null) {
	    String statement = ((ATermAppl) v0).getName();

	    DSLMethodDescription methodAlias = moptions.get(statement);

	    if (methodAlias != null) {

		ATerm secondArg;

		if (arg.getArity() < 1) {
		    AFun makeAFun = factory.makeAFun(arg.getName() + "()", 0, false);
		    secondArg = makeAFun;
		} else {
		    secondArg = arg;
		}

		ATermAppl closureBegin = factory.makeAppl(factory.makeAFun("{", 0, false));
		ATermAppl closureEnd = factory.makeAppl(factory.makeAFun("}", 0, false));

		ATermList closureArgument = factory.makeList();
		closureArgument = closureArgument.append(closureBegin);
		closureArgument = closureArgument.append(secondArg);
		closureArgument = closureArgument.append(closureEnd);
		secondArg = closureArgument;

		ATermAppl appl = factory.makeAppl(dslInvokerFunction, factory.makeAppl(factory.makeAFun(methodAlias
			.getParentClass().getSimpleName() + ".class", 0, false)), secondArg);
		return appl;
	    }
	}

	return arg;
    }

    @Override
    public ATerm transform(Context context, ATerm aterm) {
	InvokationDispatcherGroovyTransformation crt = new InvokationDispatcherGroovyTransformation(
		context.getDslMethodDescriptions());

	logger.debug("start");
	try {
	    aterm = (ATerm) aterm.accept(crt);
	} catch (VisitFailure e) {
	    logger.warn("Failed to perform transformation. Returning untransformed ATerm.", e);
	}

	return aterm;
    }

	@Override
	public Set<ATerm> getAssurances() {
		return Collections.emptySet();
	}

	@Override
	public Set<ATerm> getRequirements() {
		return Collections.emptySet();
	}

	@Override
	public Set<FileType> getSupportedFileTypes() {
	return TransformationUtils
.getSetForFiletypes(FileType.GROOVY);
	}

    @Override
    public String getDescription() {
	return "Scans the AST for method calls to "
		+ "a DSL interface and binds them to an instance of the concrete DSL by "
		+ "wrapping it into DSLInvoker.eval(DSL_CLASS,{CODE_TO_EXCUTE}). "
		+ "This supports an arbitrary combination of different DSLs and host language with no need to declare a scope for the effect of the DSL languages.";
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.INVOKATION_TRANSFORMATION;
    }
}
