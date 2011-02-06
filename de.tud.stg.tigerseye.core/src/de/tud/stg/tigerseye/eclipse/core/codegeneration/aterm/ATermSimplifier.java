package de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm;

import java.util.List;

import jjtraveler.VisitFailure;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import aterm.Visitable;

/**
 * {@link ATermSimplifier} visists the AST and simplifies the ast by combining and merging nested lists that were
 * splitted by the grammar parser, e. q. [1, [2, 3]] -> [1, 2, 3]
 * 
 * @author Kamil Erhard
 * 
 */
public class ATermSimplifier extends RecursiveVisitor {

	@Override
	public Visitable visitAppl(ATermAppl arg) throws VisitFailure {
		arg = (ATermAppl) super.visitAppl(arg);

		ATerm v0 = arg.getAnnotation(this.factory.make("LHS"));

		if (v0 != null) {
			if (v0.match(this.factory.make("\"String\"")) != null) {
				String str = arg.getName();

				if (str.startsWith("\"") && str.endsWith("\"")) {
					str = str.substring(1, str.length() - 1);
				}

				return this.factory.makeAppl(this.factory.makeAFun(str, 0, true));
			}
		}

		return arg;
	}

	@Override
	public Visitable visitList(ATermList arg) throws VisitFailure {

		arg = (ATermList) super.visitList(arg);
		arg = this.mergeMultiElementLists(arg);
		arg = this.mergeNestedStatements(arg);
		// return this.mergeSingleElementLists(v);
		return arg;
	}

	private final ATerm listMergingTemplate1 = this.factory.make("[[<list>], <term>, <fun>, <term>, [<list>]]");
	private final ATerm listMergingTemplate2 = this.factory.make("[[<list>], <term>, [<list>]]");
	private final ATerm listMergingTemplate3 = this.factory.make("[[<list>], [<list>]]");

	private ATermList mergeMultiElementLists(ATermList arg) {
		List<Object> listMerging1 = arg.match(this.listMergingTemplate1);
		List<Object> listMerging2 = arg.match(this.listMergingTemplate2);
		List<Object> listMerging3 = arg.match(this.listMergingTemplate3);

		List<Object> listMerging = (listMerging1 != null) ? listMerging1 : listMerging2 != null ? listMerging2
				: listMerging3;

		int second = listMerging1 != null ? 4 : listMerging2 != null ? 2 : 1;

		if (listMerging != null) {
			ATerm arrayAnnotation = arg.getAnnotation(this.factory.make("ARRAY"));

			if (arrayAnnotation != null) {

				ATermList list1 = (ATermList) listMerging.get(0);
				ATermList list2 = (ATermList) listMerging.get(second);

				arg = (ATermList) list1.concat(list2).setAnnotations(arg.getAnnotations());
				return arg;
			}
		}

		return arg;
	}

	ATerm statementMergingTemplate = this.factory.make("[<term>, [<list>]]");

	private ATermList mergeNestedStatements(ATermList arg) {
		List<Object> listMerging = arg.match(this.statementMergingTemplate);

		if (listMerging != null) {
			ATerm v0 = arg.getAnnotation(this.factory.make("LHS"));
			if (v0.match(this.factory.make("\"STATEMENTS\"")) != null) {
				ATerm v1 = arg.getAnnotation(this.factory.make("RHS"));
				if (v1.match(this.factory.make("\"[STATEMENT, STATEMENTS]\"")) != null) {
					ATerm element1 = (ATerm) listMerging.get(0);
					ATermList element2 = (ATermList) listMerging.get(1);

					ATermList list = this.factory.makeList();
					list = list.append(element1);

					for (int i = 0; i < element2.getLength(); i++) {
						list = list.append(element2.elementAt(i));
					}

					list = (ATermList) list.setAnnotations(arg.getAnnotations());
					arg = list;
				}
			}
		}

		return arg;
	}
}
