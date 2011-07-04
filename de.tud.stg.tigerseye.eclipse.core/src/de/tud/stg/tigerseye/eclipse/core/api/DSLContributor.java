package de.tud.stg.tigerseye.eclipse.core.api;

/**
 * Represents a contributor of a DSL definition. This will usually be either a
 * plug-in project in the current workspace or an installed Plug-in.
 * 
 * @author Leo Roos
 * 
 */
public interface DSLContributor {

    /**
     * @return the plug-in identifier
     */
    String getId();

    /**
     * @return a new instance of the {@link ClassLoaderStrategy} for this
     *         contributor.
     */
    ClassLoaderStrategy createClassLoaderStrategy();

}
