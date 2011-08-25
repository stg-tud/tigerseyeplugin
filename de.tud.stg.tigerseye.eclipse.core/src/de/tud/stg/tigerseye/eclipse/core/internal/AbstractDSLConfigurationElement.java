package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

public abstract class AbstractDSLConfigurationElement implements
	DSLConfigurationElement {

    @Override
    public String getId() {
	String attribute = getAttribute(DSLDefinitionsAttribute.Class);
	if (attribute == null)
	    throw new IllegalStateException(
		    "At least the class attribute must have been set");
	return getContributor().getId() + attribute;
    }

    @Override
    public String toString() {
	HashMap<DSLDefinitionsAttribute, String> hashMap = new HashMap<DSLDefinitionsAttribute, String>();
	for (DSLDefinitionsAttribute atts : DSLDefinitionsAttribute.values()) {
	    hashMap.put(atts, getAttribute(atts));
	}
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append(getContributor()).append(hashMap).toString();
    }
}