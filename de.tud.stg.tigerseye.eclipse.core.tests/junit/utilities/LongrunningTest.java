package utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Marker interface indicates long running tests.
 * 
 * @author Leo_Roos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LongrunningTest {
	/**
	 * optional value representing an estimate of how long this test runs in
	 * milliseconds. Whether this value is actually used depends on the rule
	 * this annotation is used with.
	 */
	int value() default -1;
}
