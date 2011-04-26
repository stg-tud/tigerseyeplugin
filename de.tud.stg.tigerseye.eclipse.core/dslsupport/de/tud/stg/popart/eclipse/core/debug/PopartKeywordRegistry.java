package de.tud.stg.popart.eclipse.core.debug;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileKeyword;

/**
 * A registry for PopartKeywords.
 * The PopartKeywordRegistry is a singleton that can be used to scan an array
 * of IMethods for public methods and fields. If such a method/field is annotated
 * as a PopartType, then a PopartKeyword is constructed from this method-/field-name
 * and configured according to the information provided by the PopartType annotation.
 * Finally the PopartKeyword is added to the registry.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartKeywordRegistry {

	private static PopartKeywordRegistry INSTANCE = new PopartKeywordRegistry();
	private Map<String, PopartKeyword> keywords = new HashMap<String, PopartKeyword>();
	private Map<Integer, PopartSourceFileKeyword> lines = new HashMap<Integer, PopartSourceFileKeyword>();

	/**
	 * Private constructor. Not to be instantiated.
	 * 
	 */
	private PopartKeywordRegistry() {
	}

	/**
	 * Get the single instance of this class.
	 * 
	 * @return The single instance
	 */
	public static PopartKeywordRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Clears the registry and constructs it from scratch for the specified IMethod array.
	 * 
	 * @param methods The methods array
	 */
	public void clearAndbuildRegistryForMethods(Method[] methods) {
		keywords.clear();
		lines.clear();		
		for (Method method : methods) {
			for (Annotation anno : method.getAnnotations()) {				
				if (anno instanceof PopartType) {		
					Class clazz = ((PopartType) anno).clazz();
					boolean breakpointPossible = (((PopartType) anno).breakpointPossible()==1);
					String methodName = method.getName();
					PopartKeyword keyword;
					try {
						keyword = (PopartKeyword) clazz.newInstance();
						keyword.setName(methodName);
						keyword.setBreakpointPossible(breakpointPossible);
						keywords.put(methodName, keyword);						
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		}		
	}

	/**
	 * Returns the Keyword associated with the specified keyword name
	 * 
	 * @param keywordName the name of the keyword
	 * @return the PopartKeyword associated with the specified name
	 */
	public PopartKeyword getKeywordByName(String keywordName) {
		return keywords.get(keywordName);
	}
	
	/**
	 * Returns a sting representation of the contents of the registry.
	 * 
	 */
	public String toString() {
		String result = "";
		for (Iterator iterator = keywords.keySet().iterator(); iterator.hasNext();) {
			Object a = iterator.next();
			result += a+" is: "+keywords.get(a)+"\n";			
		} 
		return result;
	}

}
