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
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.TextualTransformationUtils;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.MethodOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.RecursiveVisitor;

/**
 * {@link InvokationDispatcherTransformation} scans the AST for method calls to
 * a DSL interface and binds them to a instance of the concrete DSL by
 * prepending DSLInvoker.getDSL(DSL_CLASS). This supports multiple DSLs in one
 * AST.
 *
 * @author Kamil Erhard
 *
 */
public class InvokationDispatcherTransformation extends RecursiveVisitor implements ASTTransformation {
private static final Logger logger = LoggerFactory.getLogger(InvokationDispatcherTransformation.class);


	private final static AFun dslInvokerFunction;
    private Map<String, MethodOptions> moptions;

	static {
		PureFactory factory = SingletonFactory.getInstance();
		dslInvokerFunction = factory.makeAFun("DSLInvoker.getDSL", 1, false);
	}

	public InvokationDispatcherTransformation() {
	}

    public InvokationDispatcherTransformation(
	    Map<String, MethodOptions> moptions) {
	this.moptions = moptions;
	}

	public boolean areRequirementsSatisfied(Set<Class<? extends Transformation>> set) {
		return true;
	}

	public void ensures(Map<String, String> map) {

	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {
		if (arg.getArity() > 0) {
			arg = (ATermAppl) super.visitAppl(arg);
			return this.transformATerm(arg);
		} else {
			return arg;
		}
	}

	private Visitable transformATerm(ATermAppl arg) {

		ATerm v0 = arg.getAnnotation(factory.make("LHS"));

		if (v0 != null) {
			String statement = ((ATermAppl) v0).getName();

	    MethodOptions methodAlias = moptions.get(
		    statement);

			if (methodAlias != null) {

				ATermList list = factory.makeList();

				ATermAppl appl = factory.makeAppl(dslInvokerFunction, factory.makeAppl(factory.makeAFun(
						methodAlias.getParentClass().getSimpleName() + ".class", 0, false)));

				list = list.append(appl);
				list = list.append(factory.makeAppl(factory.makeAFun(".", 0, false)));
				list = list.append(arg);
				return list;
			}
		}

		return arg;
	}

	@Override
	public ATerm transform(Map<String, MethodOptions> moptions, ATerm aterm) {
		InvokationDispatcherTransformation crt = new InvokationDispatcherTransformation(moptions);

		logger.info("[InvokationDispatcher] start");
		try {
			aterm = (ATerm) aterm.accept(crt);
		} catch (VisitFailure e) {
			logger.warn("Generated log statement",e);
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
		return TextualTransformationUtils.getSetForFiletypes(FileType.JAVA);
	}

	@Override
	public String getDescription() {
		return "Scans the AST for method calls to"
				+ " a DSL interface and binds them to an instance of the concrete DSL by"
				+ " prepending DSLInvoker.getDSL(DSL_CLASS). This supports multiple DSLs in one"
				+ " AST.";
	}
}
