package utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a test that should be implemented soon and in order to
 * not get forgotten is supposed to be turned on in general but can be turned
 * off by system property
 * 
 * @author Leo_Roos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TodoTest {

	public static final String PROPERTY_KEY_TESTS_TODO = "runtests.todo";

}