package de.tud.stg.tigerseye.eclipse.core.codegeneration.utils;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;

/**
 * Class that contains utility methods for the Handling Dispatcher scope
 * classes.
 * 
 * @author Leo Roos
 * 
 */
public class HandlingDispatcherHelper {
	
	

    public static ICategory<String> getExplicitObjectHierarchy(
            IGrammar<String> grammar, Class<?> clazz) {
        	if (clazz == null) {
        		return null;
        	}
    
        	Category nodeCategory = new Category(clazz.getSimpleName(), false);
    
        	grammar.addCategory(nodeCategory);
    
        	if (clazz == Object.class) {
        		return nodeCategory;
        	}
    
        	ICategory<String> superCategory = getExplicitObjectHierarchy(grammar, clazz.getSuperclass());
        	if (superCategory != null) {
        		grammar.addRule(new Rule(superCategory, nodeCategory));
        	}
    
        	for (Class<?> c : clazz.getInterfaces()) {
        		ICategory<String> interfaceCategory = getObjectHierarchy(grammar, c);
        		if (interfaceCategory != null) {
        			grammar.addRule(new Rule(interfaceCategory, nodeCategory));
        		}
        	}
    
        	return nodeCategory;
        }

    public static ICategory<String> getObjectHierarchy(
	    IGrammar<String> grammar,
        Class<?> clazz) {
    	Category objectCategory = new Category("Object", false);
    	Category nodeCategory = new Category(clazz.getSimpleName(), false);
    	grammar.addRule(new Rule(objectCategory, nodeCategory));    
    	return nodeCategory;
    }

}
