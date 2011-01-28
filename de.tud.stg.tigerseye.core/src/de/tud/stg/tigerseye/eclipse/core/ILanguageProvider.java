package de.tud.stg.tigerseye.eclipse.core;

import java.util.List;

import javax.annotation.Nonnull;

@Nonnull
public interface ILanguageProvider {

    /**
     * Returns a list of registered DSL definitions.
     * 
     * @return all known DSL definitions. May return an empty list if none are
     *         registered.
     * 
     * @see {@link DSLDefinition}
     */
    public List<DSLDefinition> getDSLDefinitions();

    /**
     * Returns a list of DSLs according to the given extension.
     * 
     * @param extension
     *            the DSL file extension
     * @return DSLs associated with the extension
     */
    public List<DSLDefinition> getDSLForExtension(String dslName);

}
