package de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jjtraveler.VisitFailure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import aterm.ATermFactory;
import aterm.ATermList;
import aterm.pure.SingletonFactory;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.ast.Terminal;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;

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

    private final IAbstractNode head;

	public ATermBuilder(IAbstractNode head) {
	this.head = head;
	}

    /**
     * Builds and returns the term for the initialized head value.
     * 
     * @return
     * */
    public ATerm getATerm() {
	this.term = this.buildATerm(head);

	logger.debug("built ATerm: \n{}", this.term);
	try {
	    this.term = (ATerm) this.term.accept(new ATermSimplifier());

	    logger.debug("simplified ATerm: \n{}", this.term);
	} catch (VisitFailure e) {
	    logger.warn("Generated log statement", e);
	}
	return this.term;
    }

	int index = 0;

    private ATerm buildATerm(IAbstractNode node) {
	int oldIndex = this.index;
		this.index = 0;

		if (node instanceof Terminal) {
			Terminal t = (Terminal) node;
	    if (t.getTerm() == null) {
		logger.error("getTerm for {} is null ignoring it", t);
		// XXX(Leo_Roos;Nov 18, 2011) should return to avoid NullPoint,
		// what would be a valid return value?
	    }

	  // XXX(Leo_Roos;Nov 18, 2011) would it make difference just to use
	    // fac.make(t.getTerm())
	    ATerm term = fac.makeAppl(fac.makeAFun(t.getTerm(), 0, false));

	    String lhsName = t.getItem().getRule().getLhs()
					.getName();
	    term = term.setAnnotation(fac.make("LHS"), fac.makeAppl(fac.makeAFun(lhsName, 0, true)));

			String rhsName = t.getItem().getRule().getRhs().get(this.index).getName();
	    term = term.setAnnotation(fac.make("RHS"), fac.makeAppl(fac.makeAFun(rhsName, 0, true)));

			this.index = oldIndex;
			return term;
		} else {
			ATermList list = fac.makeList();

			for (IAbstractNode child : node.getChildren()) {
				list = list.append(this.buildATerm(child));
				this.index++;
			}

	    String lhsName = node.getItem().getRule().getLhs().getName();
	    list = (ATermList) list.setAnnotation(fac.make("LHS"), fac.makeAppl(fac.makeAFun(lhsName, 0, true)));
	    String rhsListString = node.getItem().getRule().getRhs().toString();
	    list = (ATermList) list.setAnnotation(fac.make("RHS"),
		    fac.makeAppl(fac.makeAFun(rhsListString.substring(1, rhsListString
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

	if (lhsName.equals("Closure")
		|| lhsName.equals(CategoryNames.PROGRAM_CATEGORY)) {
			List<ICategory<String>> rhs = node.getItem().getRule().getRhs();

	    return rhs.size() == 1
		    && rhs.get(0).getName()
			    .equals(CategoryNames.STATEMENTS_CATEGORY);
		}

		return false;
	}
}
