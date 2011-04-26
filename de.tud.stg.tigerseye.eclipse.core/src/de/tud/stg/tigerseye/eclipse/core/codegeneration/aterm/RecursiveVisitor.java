package de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm;

import jjtraveler.VisitFailure;
import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.ATermLong;
import aterm.ATermPlaceholder;
import aterm.ATermReal;
import aterm.Visitable;
import aterm.Visitor;
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
	public Visitable visitList(ATermList arg) throws VisitFailure {

		// save annotations
		ATermList annotations = arg.getAnnotations();

		for (int i = 0; i < arg.getChildCount(); i++) {
			ATerm t = (ATerm) arg.elementAt(i).accept(this);

			if (t != arg.elementAt(i)) {
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
