package de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm;

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

public class PrettyGroovyCodePrinter implements Visitor, CodePrinter {

	private StringBuilder sb = new StringBuilder();
	protected final ATermFactory factory = SingletonFactory.getInstance();
	private int depth = 0;

	public PrettyGroovyCodePrinter() {
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

		boolean isDSLMethod = arg.getArity() > 0;
		boolean isDSLLiteral = arg.getArity() == 0 && arg.getName().startsWith("get");

		if (isDSLMethod) {
			this.sb.append("(\n");

			for (ATerm child : arg.getArgumentArray()) {
				child.accept(this);
				this.sb.append(",\n");
			}

			this.sb.delete(this.sb.length() - 2, this.sb.length());
			this.sb.append(")");
		} else if (isDSLLiteral) {
			this.sb.append("()\n");
		}

		this.depth--;
		return arg;
	}

	@Override
	public Visitable visitList(ATermList arg) throws VisitFailure {

		ATerm v0 = arg.getAnnotation(this.factory.make("CLOSURE"));

		if (v0 != null) {
			return this.visitProgramStatements(arg);
		}

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

		this.sb.append("[\n");

		for (int i = 0; i < arg.getLength(); i++) {
			arg.elementAt(i).accept(this);

			this.sb.append(",\n");
		}

		this.sb.delete(this.sb.length() - 2, this.sb.length());

		this.sb.append("\n");
		this.depth--;
		this.sb.append("] as " + arrayType + "[]");

		return arg;
	}

	private Visitable visitProgramStatements(ATermList arg) throws VisitFailure {
		if (this.depth > 0) {
			return this.visitClosure(arg);
		}

		for (int i = 0; i < arg.getLength(); i++) {
			arg.elementAt(i).accept(this);
		}

		return arg;
	}

	private Visitable visitClosure(ATermList arg) throws VisitFailure {
		this.depth++;
		this.sb.append("{\n");

		for (int i = 0; i < arg.getLength(); i++) {
			arg.elementAt(i).accept(this);
		}

		this.sb.append("}\n");
		this.depth--;

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