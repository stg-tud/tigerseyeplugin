package utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate this is a plug-in test, which requires the
 * Platform to be running.
 * 
 * @author Leo Roos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PluginTest {
	// what are you looking at, there's nothing to see here
}
