package de.tud.stg.popart.eclipse.editor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * MethodsComparator is used by sorting methods alphabetically.
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 */
public class MethodsComparator implements Comparator<Method>, Serializable {

	public int compare(Method method0, Method method1) {
		return method0.getName().compareTo(method1.getName());
	}
}