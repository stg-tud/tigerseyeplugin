package de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm;

import jjtraveler.VisitFailure;
import aterm.*;
import aterm.pure.SingletonFactory;

public class RecursiveVisitor implements Visitor {

	protected final ATermFactory factory = SingletonFactory.getInstance();

	@Override
	public Visitable visitAFun(AFun fun) throws VisitFailure {
		return fun;
	}

	@Override
	public Visitable visitATerm(ATerm arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {

		AFun fun = (AFun) arg.getAFun().accept(this);

		ATerm[] arguments = arg.getArgumentArray();

		if (fun != arg.getAFun()) {
			arg = arg.getFactory().makeAppl(fun, arguments);
		}

		for (int i = 0; i < arguments.length; i++) {
			Visitable v = arguments[i].accept(this);

			if (v != arguments[i]) {
				arg = (ATermAppl) arg.setChildAt(i, v);
			}
		}

		return arg;
	}

	@Override
	public Visitable visitBlob(ATermBlob arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitInt(ATermInt arg) throws VisitFailure {
		return arg;
	}

	@Override
    // XXX(Leo_Roos;Nov 18, 2011) leads to many recursive calls, a possible
    // reason why there has to be such a big stack size in order to run
    // Tigerseye
	public Visitable visitList(ATermList arg) throws VisitFailure {
		// save annotations
		ATermList annotations = arg.getAnnotations();

		int childCount = arg.getChildCount();
		for (int i = 0; i < childCount; i++) {
	    ATerm currentAterm = arg.elementAt(i);
	    ATerm t = (ATerm) currentAterm.accept(this);

			if (t != currentAterm) {
				arg = arg.replace(t, i);
			}
		}

		// restore annotations
		arg = (ATermList) arg.setAnnotations(annotations);

		return arg;
	}

	@Override
	public Visitable visitLong(ATermLong arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitPlaceholder(ATermPlaceholder arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitReal(ATermReal arg) throws VisitFailure {
		return arg;
	}

	@Override
	public jjtraveler.Visitable visit(jjtraveler.Visitable any) throws VisitFailure {
		return any;
	}

}
