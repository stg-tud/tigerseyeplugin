package de.tud.stg.tigerseye.eclipse.core;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeException;

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
