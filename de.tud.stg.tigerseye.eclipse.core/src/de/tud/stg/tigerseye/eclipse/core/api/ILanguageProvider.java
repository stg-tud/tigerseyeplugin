package de.tud.stg.tigerseye.eclipse.core.api;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;


@Nonnull
public interface ILanguageProvider {

    /**
     * Returns a set of registered DSL definitions.
     * 
     * @return all known DSL definitions. May return an empty list if none are
     *         registered.
     * 
     * @see {@link DSLDefinition}
     */
    public Set<DSLDefinition> getDSLDefinitions();

    /**
     * Returns the currently active {@link DSLDefinition} for extension
     * {@code extension}.
     * 
     * @param extension
     *            the DSL file extension
     * @return {@code DSLDefinition} associated with the extension or
     *         <code>null</code> if none is active or none can be found.
     * @throws TigerseyeRuntimeException
     *             if more than one DSL is active for {@code extension}, which
     *             is an illegal and should be an unreachable state.
     */
    public @CheckForNull
    DSLDefinition getActiveDSLForExtension(String extension);

}
