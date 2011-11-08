package de.tud.stg.tigerseye.eclipse.core.api;

import javax.annotation.Nonnull;

public interface ClassLoaderStrategy {

    /**
     * Tries to load class of given name.
     * <p>
     * Implementors should catch Throwables when accessing a {@code loadClass}
     * Method. Otherwise an ill configured DSL plug-in might cause unhandled
     * runtime exceptions which again will render the plug-in unusable.
     * 
     * @param className
     * @return the loaded class
     * @throws ClassNotFoundException
     *             if class can not be loaded or a {@code Throwable} of
     *             <i>any</i> other kind is thrown encapsulated in a
     *             {@link ClassNotFoundException}.
     */
    @Nonnull
    Class<?> loadClass(String className) throws ClassNotFoundException;

}
