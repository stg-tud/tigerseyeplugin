/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.listsets;

import de.tud.stg.popart.dslsupport.*;

/**
 * This class defines a DSL environment for working with List as mathematical sets policies.
 */
public class ListSetsDSL implements DSL {

	def DEBUG = false; 
	 
	public static Interpreter getInterpreter(HashMap context) {
		DSLCreator.getInterpreter(new ListSetsDSL(),context)
	}
	
	/* Literals */
	
	public LinkedList getEMPTY_SET() {
		return new LinkedList();
	}
	
	public LinkedList getSET_CONTAINING_EMPTY_SET() {
		List result = new LinkedList();
		result.add(getEMPTY_SET());
		return result;
	}
	
	/* Operations */
	
	public List union(List left, List right) {
		List result = new LinkedList();
		result.addAll(left);
		result.addAll(right);
		return result;
	}
	
	public LinkedList intersection(LinkedList left, LinkedList right) {
		LinkedList result = new LinkedList();
		result.addAll(left);
		result.retainAll(right);
		return result;
	}
	
	public List difference(List left, List right) {
		List result = new LinkedList(left);
		result.removeAll(right);
		return result;
	}
	
	public Object conjunction(LinkedList lst) {
		if (lst.isEmpty()) return null;
		LinkedList elems = new LinkedList(lst);
		Object first = elems.get(0);
		elems.remove(0);
		return elems.inject(first) { con, elem -> con & elem }
	}
	
	public Object disjunction(LinkedList lst) {
		if (lst.isEmpty()) return null;
		LinkedList elems = new LinkedList(lst);
		Object first = elems.get(0);
		elems.remove(0);
		return elems.inject(first) { dis, elem -> dis | elem }
	}

	public LinkedList addToSets(LinkedList sets, Object elem) {
		LinkedList result = new LinkedList();
		sets.each { set ->
			LinkedList setWithAddedElem = new LinkedList(set);
			setWithAddedElem.add(elem);
			result.add(setWithAddedElem);
		}
		return result;
	}
	
	public LinkedList powerSet(List lst) {
		LinkedList result = new LinkedList();

		if (lst.size() == 0) {
			result.add(new LinkedList()); //add empty set 
			return result;
		} else if (lst.size() == 1) {
			result.add(new LinkedList()); //add empty set 
			result.add(lst); //add set only containing the one conatined element
			return result;
		} else {
			LinkedList elems = new LinkedList(lst);
			Object first = elems.get(0);
			elems.remove(0);
			
			LinkedList powerSetElems = powerSet(elems);
			LinkedList powerSetWithoutFirst = addToSets(new LinkedList(powerSetElems),first);  
			LinkedList powerSetWithFirst = new LinkedList(powerSetElems);  
			
			return union(powerSetWithoutFirst,powerSetWithFirst);
		}	
	}
		
    public convertToDNF(List subSets) {
    	//does not normalize() DNF internally (so the result is not really in DNF)
    	Object conList = new LinkedList();
    	subSets.each { subSet ->
        	List celems = new LinkedList(subSet); 
    	    if (DEBUG) println "celems=$celems"
        	Object conjuncted = conjunction(celems);
            if (DEBUG) println "conjuncted=$conjuncted"
        	conList.add(conjuncted);    	
            if (DEBUG) println "conList=$conList\n\n"
    	}
    	Object result = disjunction(conList);
    	return result;
    }
}

