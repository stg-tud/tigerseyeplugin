package de.tud.stg.tigerseye.eclipse.core.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;

public class DSLDefinitionImpl implements DSLDefinition {

    private static final Logger logger = LoggerFactory
	    .getLogger(DSLDefinitionImpl.class);

    private final String classPath;
    // private final String contributorSymbolicName;
    private final String dslName;
    private IPreferenceStore store;

    @Nullable
    private ClassLoaderStrategy classloaderStrategy;

    private final DSLConfigurationElement configurationElement;

    /**
     * Constructs a {@link DSLDefinition} for given values, <code>null</code> is
     * never valid.
     * 
     * @param classPath
     * @param dslName
     * @param classloaderStrategy
     */
    @Nonnull
    public DSLDefinitionImpl(String classPath,
	    DSLConfigurationElement dslconfel, String dslName) {
	this.classPath = classPath;
	this.dslName = dslName;
	this.configurationElement = dslconfel;
    }

    @Override
    public String getClassPath() {
	return classPath;
    }

    @Override
    public String getContributorSymbolicName() {
	return this.configurationElement.getContributor().getId();
    }

    @Override
    public String getLanguageKey() {
	return this.configurationElement.getId();
    }

    @Override
    public String getDslName() {
	return dslName;
    }

    @Override
    public String toString() {
	return this.dslName;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (obj == this) {
	    return true;
	}
	if (obj.getClass() == this.getClass()) {
	    DSLDefinitionImpl other = (DSLDefinitionImpl) obj;
	    return new EqualsBuilder()//
		    .append(getLanguageKey(), other.getLanguageKey())//
		    .isEquals();
	} else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(345, 13)//
		.append(getLanguageKey());
	return hashCodeBuilder.toHashCode();
    }

    @Override
    public String getKeyFor(DSLKey<?> dslKey) {
	return getLanguageKey() + dslKey.suffix;
    }

    @Override
    public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFoundException {
	T value = key.getValue(this, getStore());
	return value;
    }

    @Override
    public <T> void setValue(DSLKey<T> key, T value) {
	key.setValue(this, getStore(), value);
    }

    @Override
    public void setToDefault(DSLKey<?> key) {
	getStore().setToDefault(getKeyFor(key));
    }

    private IPreferenceStore getStore() {
	if (this.store == null) {
	    throw new IllegalStateException("Store not set yet");
	}
	return this.store;
    }

    public void setStore(IPreferenceStore store) {
	this.store = store;
    }

    @Override
    public boolean isActive() {
	return DSLActivationState.getValue(this, getStore());
    }

    @Override
    public void setActive(boolean active) {
	DSLActivationState.setValue(this, getStore(), active);
    }

    @Override
    public Class<? extends DSL> loadClass() {
	try {
	    Class<?> loadClass = getClassLoaderStrategy().loadClass(
		    getClassPath());
	    /*
	     * The cast must be successful or else the DSL language is erroneous
	     * and an exception appropriate
	     */
	    @SuppressWarnings("unchecked")
	    Class<? extends DSL> dslClass = (Class<? extends DSL>) loadClass;
	    return dslClass;
	} catch (ClassNotFoundException e) {
	    throw new TigerseyeRuntimeException("DSLDefinition "
		    + this.toString() + " is not loadable", e);
	}
    }

    private ClassLoaderStrategy getClassLoaderStrategy() {
	if (classloaderStrategy == null) {
	    classloaderStrategy = getContributor().createClassLoaderStrategy();
	}
	return classloaderStrategy;
    }

    @Override
    public DSLContributor getContributor() {
	return this.configurationElement.getContributor();
    }

    @Override
    public String getIdentifer() {
	return getLanguageKey();
    }

}