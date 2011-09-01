package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link SetDSL} is a small DSL showing the possibility of modeling
 * mathematical sets
 *
 * @author Kamil Erhard
 *
 */
@DSLClass(whitespaceEscape = " ", waterSupported=true)
public class SetDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	public static class MyList {
		String head;
		MyList tail;

		public MyList(String head, MyList tail) {
			this.head = head;
			this.tail = tail;
		}

		public String[] toArray() {
			LinkedList<String> list = new LinkedList<String>();

			MyList current = this;

			do {
				list.add(current.head);
				current = current.tail;
			} while (current != null);

			return list.toArray(new String[list.size()]);
		}
	}

	@DSLMethod(production = "p0 ⋃ p1")
	public Set union(Set a, Set b) {
		HashSet set = new HashSet(a);
		set.addAll(b);
		return set;
	}

	@DSLMethod(production = "p0  ⋂  p1")
	public Set intersection(Set a, Set b) {
		HashSet set = new HashSet(a);
		set.addAll(b);
		return set;
	}

	@DSLMethod(production = "{  p0  }")
	public Set asSet(MyList a) {
		return new HashSet(Arrays.asList(a.toArray()));
	}

	@DSLMethod(production = "p0", topLevel = false)
	public MyList singleElementedList(String head) {
		return new MyList(head, null);
	}

	@DSLMethod(production = "p0  ,  p1", topLevel =false)
	public MyList multiElementedList(String head, MyList tail) {
		return new MyList(head, tail);
	}
}
