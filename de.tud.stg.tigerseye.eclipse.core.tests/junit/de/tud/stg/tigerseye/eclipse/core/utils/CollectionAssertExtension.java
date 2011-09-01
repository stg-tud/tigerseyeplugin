package de.tud.stg.tigerseye.eclipse.core.utils;

import java.util.Collection;

import org.fest.assertions.CollectionAssert;

public class CollectionAssertExtension extends CollectionAssert{

	protected CollectionAssertExtension(Collection<?> actual) {
		super(actual);
	}

	public CollectionAssertExtension containsOnly(Collection<?> collections){
		Object[] array = collections.toArray();
		containsOnly(array);
		return this;
	}
	
	public CollectionAssertExtension contains(Collection<?> collections){
		Object[] array = collections.toArray();
		contains(array);
		return this;
	}	
	
}
