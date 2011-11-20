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
import de.tud.stg.tigerseye.eclipse.core.api.TransformationConstants;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.TextualTransformationUtils;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.RecursiveVisitor;

/**
 * {@link ClosureResultTransformation} scans the AST for method calls to a DSL
 * interface and encapsulates them into closures. This supports multiple DSLs in
 * one AST.
 * 
 * @author Kamil Erhard
 * 
 */
public class ClosureResultTransformation extends RecursiveVisitor implements ASTTransformation {
private static final Logger logger = LoggerFactory.getLogger(ClosureResultTransformation.class);


	private final static ATermAppl closureEnd;
	private final static ATermAppl closureBegin;
	private final static AFun dslInvokerFunction;

	private ATermList dslInvokerClasses;
    private Map<String, DSLMethodDescription> moptions;

	static {
		PureFactory factory = SingletonFactory.getInstance();
	String dslInvokerName = DSLInvoker.class.getSimpleName();
	dslInvokerFunction = factory.makeAFun(dslInvokerName + ".eval", 2, false);
		closureBegin = factory.makeAppl(factory.makeAFun("{", 0, false));
		closureEnd = factory.makeAppl(factory.makeAFun("}", 0, false));
	}

	public ClosureResultTransformation() {
	}

    public ClosureResultTransformation(Map<String, DSLMethodDescription> moptions) {
	this.moptions = moptions;
		dslInvokerClasses = factory.makeList();
	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {
		if (arg.getArity() > 0) {
			return this.transformATerm(arg);
		} else {
	    // should probably be also transformed for properties
			return arg;
		}
	}

	private Visitable transformATerm(ATermAppl arg) {

		ATerm v0 = arg.getAnnotation(factory.make("LHS"));

		if (v0 != null) {
			String statement = ((ATermAppl) v0).getName();

	    DSLMethodDescription methodAlias = moptions.get(statement);

			if (methodAlias != null) {

				Class<?> parentClass = methodAlias.getParentClass();
				AFun makeAFun = factory.makeAFun(parentClass
						.getSimpleName()
						+ ".class", 0, false);
				ATermAppl dslInvokerClasses = factory.makeAppl(makeAFun);

				ATermList closureArgument = factory.makeList();
				closureArgument = closureArgument.append(closureBegin);
				closureArgument = closureArgument.append(arg);
				closureArgument = closureArgument.append(closureEnd);

				ATermAppl makeAppl = factory.makeAppl(dslInvokerFunction, dslInvokerClasses, closureArgument);
				return makeAppl;
			}
		}

		return arg;
	}

    @Override
    public ATerm transform(Map<String, DSLMethodDescription> moptions, ATerm aterm) {
	ClosureResultTransformation crt = new ClosureResultTransformation(
		moptions);

	logger.debug("start ClosureResultTransformation");
	try {
	    aterm = (ATerm) aterm.accept(crt);
	} catch (VisitFailure e) {
	    logger.warn("Failed visiting ClosureResultTransformation", crt, e);
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
	public Set<TransformationType> getSupportedFileTypes() {
	return TextualTransformationUtils.getSetForFiletypes(FileType.GROOVY, FileType.TIGERSEYE);
	}

	@Override
    public String getDescription() {
	return "Scans the AST for method calls to a DSL interface and encapsulates them into closures";
    }

    @Override
    public int getBuildOrderPriority() {
	return TransformationConstants.CLOSURE_RESULT_TRANSFORMATION;
    }
}
