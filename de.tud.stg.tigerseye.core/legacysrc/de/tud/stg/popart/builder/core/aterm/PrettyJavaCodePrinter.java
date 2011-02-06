package de.tud.stg.popart.builder.core.aterm;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

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

public class PrettyJavaCodePrinter implements Visitor, CodePrinter {

	private StringBuilder sb = new StringBuilder();
	protected final ATermFactory factory = SingletonFactory.getInstance();
	private int depth = 0;

	public PrettyJavaCodePrinter() {
	}

	public void write(OutputStream out) {
		PrintWriter printWriter = new PrintWriter(out);

		printWriter.write(this.sb.toString());
		printWriter.flush();
		this.sb = new StringBuilder();
	}

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {
		this.depth++;

		arg.getAFun().accept(this);

		if (arg.getArity() > 0) {
			this.sb.append("(\n");

			for (ATerm child : arg.getArgumentArray()) {
				child.accept(this);
				this.sb.append(",\n");
			}

			this.sb.delete(this.sb.length() - 2, this.sb.length());
			this.sb.append(")");
		}

		this.depth--;
		return arg;
	}

	@Override
	public Visitable visitList(ATermList arg) throws VisitFailure {
		ATermAppl arrayAnnotation = this.getArrayAnnotation(arg);

		if (arrayAnnotation != null) {
			return this.visitArray(arg, arrayAnnotation.getName());
		}

		for (int i = 0; i < arg.getLength(); i++) {
			arg.elementAt(i).accept(this);
		}

		return arg;
	}

	private Visitable visitArray(ATermList arg, String arrayType) throws VisitFailure {
		this.depth++;

		this.sb.append("new " + arrayType + "[] {\n");

		for (int i = 0; i < arg.getLength(); i++) {
			arg.elementAt(i).accept(this);

			this.sb.append(",\n");
		}

		this.sb.delete(this.sb.length() - 2, this.sb.length());

		this.sb.append("\n");
		this.depth--;
		this.sb.append("}");

		return arg;
	}

	private final ATerm arrayListTemplate = this.factory.make("[<list>]");

	private ATermAppl getArrayAnnotation(ATermList arg) {
		List<Object> listMerging = arg.match(this.arrayListTemplate);

		if (listMerging != null) {
			ATermAppl arrayAnnotation = (ATermAppl) arg.getAnnotation(this.factory.make("ARRAY"));

			return arrayAnnotation;
		}

		return null;
	}

	@Override
	public Visitable visitAFun(AFun fun) throws VisitFailure {
		if (fun.isQuoted()) {
			this.sb.append("\"" + fun.getName() + "\"");
		} else {
			this.sb.append(fun.getName());
		}
		return fun;
	}

	@Override
	public Visitable visitATerm(ATerm arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitBlob(ATermBlob arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitInt(ATermInt arg) throws VisitFailure {
		this.sb.append(arg.getInt());
		return arg;
	}

	@Override
	public Visitable visitLong(ATermLong arg) throws VisitFailure {
		this.sb.append(arg.getLong());
		return arg;
	}

	@Override
	public Visitable visitPlaceholder(ATermPlaceholder arg) throws VisitFailure {
		return arg;
	}

	@Override
	public Visitable visitReal(ATermReal arg) throws VisitFailure {
		this.sb.append(arg.getReal());
		return arg;
	}

	@Override
	public jjtraveler.Visitable visit(jjtraveler.Visitable any) throws VisitFailure {
		return any;
	}
}