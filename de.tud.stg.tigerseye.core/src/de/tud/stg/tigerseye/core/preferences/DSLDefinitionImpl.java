package de.tud.stg.tigerseye.core.preferences;

import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.DSLKey;
import de.tud.stg.tigerseye.core.NoLegalPropertyFound;

public class DSLDefinitionImpl implements DSLDefinition {

    private final String classPath;
    private final String contributorSymbolicName;
    private final String languageKey;
    private final String dslName;
    private final HashMap<DSLKey<?>, Object> keyValueMap;
    private IPreferenceStore store;

    /**
     * Constructs a {@link DSLDefinition} for given values, <code>null</code> is
     * never valid.
     * 
     * @param extension
     * @param classPath
     * @param contributorSymbolicName
     * @param dslName
     * @param color
     * @param languageKey
     */
    public DSLDefinitionImpl(String classPath, String contributorSymbolicName,
	    String dslName, String languageKey) {
	this.classPath = classPath;
	this.contributorSymbolicName = contributorSymbolicName;
	this.dslName = dslName;
	this.languageKey = languageKey;
	this.keyValueMap = new HashMap<DSLKey<?>, Object>();
    }

    @Override
    public String getClassPath() {
	return classPath;
    }

    @Override
    public String getContributorSymbolicName() {
	return contributorSymbolicName;
    }

    @Override
    public String getLanguageKey() {
	return languageKey;
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
	if (obj == null)
	    return false;
	if (obj instanceof DSLDefinitionImpl) {
	    DSLDefinitionImpl other = (DSLDefinitionImpl) obj;
	    return new EqualsBuilder()//
		    .append(this.classPath, other.classPath)//
		    .append(this.contributorSymbolicName,
			    other.contributorSymbolicName)//
		    .isEquals();
	}
	return false;
    }

    @Override
    public int hashCode() {
	HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(345, 13)//
		.append(this.classPath)//
		.append(this.contributorSymbolicName);
	return hashCodeBuilder.toHashCode();
    }

    @Override
    public String getKeyFor(DSLKey<?> dslKey) {
	return getLanguageKey() + dslKey.suffix;
    }

    @Override
    public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFound {
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
	if (this.store == null)
	    throw new IllegalStateException("Store not set yet");
	return this.store;
    }

    public void setStore(IPreferenceStore store) {
	this.store = store;
    }

    @Override
    public boolean isActive() {
	return getStore().getBoolean(getKeyFor(DSLKey.LANGUAGE_ACTIVE));
    }

    @Override
    public void setData(DSLKey<?> key, Object value) {
	this.keyValueMap.put(key, value);
    }

    @Override
    public Object getData(DSLKey<?> key) {
	return this.keyValueMap.get(key);
    }

}