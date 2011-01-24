package de.tud.stg.popart.builder.core.aterm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jjtraveler.VisitFailure;
import aterm.ATerm;
import aterm.ATermFactory;
import aterm.ATermList;
import aterm.pure.SingletonFactory;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.ast.Terminal;
import de.tud.stg.parlex.core.ICategory;

/**
 * {@link ATermBuilder} builds an ATERM AST out of the generated earley parser tree.
 * 
 * @author Kamil Erhard
 * 
 */
public class ATermBuilder {
private static final Logger logger = LoggerFactory.getLogger(ATermBuilder.class);


	private static ATermFactory fac = SingletonFactory.getInstance();
	private ATerm term;
	private final boolean DEBUG = false;

	public ATermBuilder(IAbstractNode head) {
		this.term = this.buildATerm(head);

		if (this.DEBUG)
			logger.info("built ATerm: " + this.term);
		try {
			this.term = (ATerm) this.term.accept(new ATermSimplifier());
			if (this.DEBUG)
				logger.info("simplified ATerm: " + this.term);
		} catch (VisitFailure e) {
			logger.warn("Generated log statement",e);
		}
	}

	public ATerm getATerm() {
		return this.term;
	}

	int index = 0;

	public ATerm buildATerm(IAbstractNode node) {
		int oldIndex = this.index;
		this.index = 0;

		if (node instanceof Terminal) {
			Terminal t = (Terminal) node;
			if (t.getTerm() == null) {
				logger.error("getTerm for {} is null ", t);
			}

			ATerm term = fac.makeAppl(fac.makeAFun(t.getTerm(), 0, false));

			term = term.setAnnotation(fac.make("LHS"), fac.makeAppl(fac.makeAFun(t.getItem().getRule().getLhs()
					.getName(), 0, true)));

			term = term.setAnnotation(fac.make("RHS"), fac.makeAppl(fac.makeAFun(t.getItem().getRule().getRhs().get(
					this.index).toString(), 0, true)));

			this.index = oldIndex;
			return term;
		} else {
			ATermList list = fac.makeList();

			for (IAbstractNode child : node.getChildren()) {
				list = list.append(this.buildATerm(child));
				this.index++;
			}

			list = (ATermList) list.setAnnotation(fac.make("LHS"), fac.makeAppl(fac.makeAFun(node.getItem().getRule()
					.getLhs().getName(), 0, true)));
			String tmp = node.getItem().getRule().getRhs().toString();
			list = (ATermList) list.setAnnotation(fac.make("RHS"), fac.makeAppl(fac.makeAFun(tmp.substring(1, tmp
					.length() - 1), 0, true)));

			if (this.isArray(node)) {
				list = (ATermList) list.setAnnotation(fac.make("ARRAY"), fac.make(node.getItem().getRule().getLhs()
						.getName()));
			}
			if (this.isClosure(node)) {
				list = (ATermList) list.setAnnotation(fac.make("CLOSURE"), fac.makeInt(1));
			}

			this.index = oldIndex;
			return list;
		}
	}

	private static final Pattern arrayPattern = Pattern.compile("([^_\\s]+)\\[\\]");

	private boolean isArray(IAbstractNode node) {
		String lhsName = node.getItem().getRule().getLhs().getName();

		Matcher m = arrayPattern.matcher(lhsName);
		return m.find();
	}

	private boolean isClosure(IAbstractNode node) {
		String lhsName = node.getItem().getRule().getLhs().getName();

		if (lhsName.equals("Closure") || lhsName.equals("PROGRAM")) {
			List<ICategory<String>> rhs = node.getItem().getRule().getRhs();

			return rhs.size() == 1 && rhs.get(0).getName().equals("STATEMENTS");
		}

		return false;
	}
}
