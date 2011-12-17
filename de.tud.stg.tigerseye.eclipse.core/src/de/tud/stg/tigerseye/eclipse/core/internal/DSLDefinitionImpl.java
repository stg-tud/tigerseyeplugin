package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;

/*
 * FIXME(Leo_Roos;Oct 19, 2011) need a wrapper around Class which handles all runtime exceptions. These might occur when an ill configured DSL is loaded.
 *
 */
public class DSLDefinitionImpl implements DSLDefinition {

    private static final Logger logger = LoggerFactory.getLogger(DSLDefinitionImpl.class);

    private final String classPath;
    // private final String contributorSymbolicName;
    private final String dslName;
    private IPreferenceStore store;

    /*
     * lazy initialized
     */
    @Nullable
    private ClassLoaderStrategy classloaderStrategy;

    private final DSLConfigurationElement configurationElement;

    /*
     * lazy initialized
     */
    private @Nullable
    Class<? extends DSL> dslClass;

    /**
     * Constructs a {@link DSLDefinition} for given values, <code>null</code> is
     * never valid.
     * 
     * @param classPath
     * @param dslName
     * @param classloaderStrategy
     */
    @Nonnull
    public DSLDefinitionImpl(String classPath, DSLConfigurationElement dslconfel, String dslName) {
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
    public Class<? extends DSL> getDSLClassChecked() {
	if (dslClass == null) {
	    if (isDSLClassLoadable()) {
		try {
		    dslClass = loadClassRaw();
		} catch (Exception e) {
		    throw new TigerseyeRuntimeException("Catched unexpected exception while trying to load " + this
			    + ". Unexpected because a prior loadability check has been made.");
		}
	    }
	}
	return dslClass;
    }

    /**
     * @return loaded class
     * 
     * @throws Exception
     *             if class could not be loaded
     */
    public @Nonnull
    Class<? extends DSL> loadClassRaw() throws Exception {
	Class<?> loadClass = getClassLoaderStrategy().loadClass(getClassPath());
	Assert.isNotNull(loadClass);
	/*
	 * The cast must be successful or else the DSL language is erroneous and
	 * an exception appropriate
	 */
	@SuppressWarnings("unchecked")
	Class<? extends DSL> dslClass = (Class<? extends DSL>) loadClass;
	return dslClass;
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

    @Override
    public boolean isDSLClassLoadable() {
	DSLDefinitionImpl dsl = this;
	try {
	    // Check existence
	    @SuppressWarnings("unchecked")
	    Class<? extends DSL> loadClass = (Class<? extends DSL>) getClassLoaderStrategy().loadClass(getClassPath());
	    /*
	     * Cannot do the next check since that would also execute possible
	     * logic within the constructor
	     */
	    // loadClass.newInstance();
	    logIfClassHasNoZeroArgConstructor(loadClass);
	} catch (Exception e) {
	    logger.warn("Could not access registered DSL {} with class {} of plug-in {}."
		    + "It will be ignored. Check your configuration."
		    + "Is the specified class on the classpath (compileerrors?). "
		    + "Is the correct DSL class name given? Is the host-plug-in accessible?",
		    new Object[] { dsl.getDslName(), dsl.getClassPath(), dsl.getContributor().getId(), e });
	    return false;
	}
	ModelEntry findEntry = PluginRegistry.findEntry(dsl.getContributor().getId());
	IPluginModelBase model = null;
	if (findEntry == null) {
	    // dsl not yet loaded
	    return false;
	} else {
	    model = findEntry.getModel();
	}

	if (model == null) {
	    logger.error("No plugin definition for given id {} can be found", dsl.getContributor().getId());
	    return false;
	} else {
	    String installLocation = model.getInstallLocation();
	    File file = new File(installLocation);
	    if (!file.exists()) {
		logger.warn("location of plugin {} not found. Can not add DSL {} to classpath;"
			+ "Consider not to change the location of an active plug-in ;)", dsl.getContributor().getId(),
			dsl.getDslName());
		return false;
	    }
	}
	return true;
    }

    private void logIfClassHasNoZeroArgConstructor(Class<? extends DSL> loadClass) {
	Constructor<?>[] constructors;
	try {
	    constructors = loadClass.getConstructors();
	} catch (NoClassDefFoundError e) {
	    logger.warn("could not determine whether class has zero argument constructor.", e);
	    return;
	}
	if (constructors.length < 1) {
	    logger.warn("DSL Class has no public contructor. Tigerseye expects a constructor with zero arguments");
	} else {
	    boolean hasZeroArgConstructor = false;
	    for (Constructor<?> constructor : constructors) {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		if (parameterTypes.length < 1) {
		    hasZeroArgConstructor = true;
		    break;
		}
	    }
	    if (!hasZeroArgConstructor) {
		logger.warn("DSL Class has no public contructor with zero arguments."
			+ "Tigerseye expects a public constructor with zero arguments");
	    }
	}
    }

}