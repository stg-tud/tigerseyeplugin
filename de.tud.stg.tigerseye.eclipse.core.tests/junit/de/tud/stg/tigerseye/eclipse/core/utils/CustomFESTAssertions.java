package de.tud.stg.tigerseye.eclipse.core.utils;

import java.util.Collection;

import org.fest.assertions.Assertions;


public class CustomFESTAssertions extends Assertions{

	  public static StringAssertExtensions assertThat(String actual) {
		    return new StringAssertExtensions(actual);
	  }
	  
	  
	  public static CollectionAssertExtension assertThat(Collection<?> actual) {
		    return new CollectionAssertExtension(actual);
	  }
	  
}
