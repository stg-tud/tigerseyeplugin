package utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.tud.stg.tigerseye.util.OSInfo.OSType;

/**
 * Annotating a test that relies on a specific OS or on a set of OSs.
 * 
 * @author Leo_Roos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OSDependent {

	/**
	 * @return list of supported operating systems
	 */
	OSType[] value() default { OSType.UNKNOWN };

}
