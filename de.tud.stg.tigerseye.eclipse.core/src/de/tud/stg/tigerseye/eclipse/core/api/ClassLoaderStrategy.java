package de.tud.stg.tigerseye.eclipse.core.api;

public interface ClassLoaderStrategy {

    Class<?> loadClass(String className) throws ClassNotFoundException;

}
