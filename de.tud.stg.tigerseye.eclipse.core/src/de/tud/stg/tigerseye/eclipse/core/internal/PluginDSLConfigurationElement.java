package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;

import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

public class PluginDSLConfigurationElement extends
	AbstractDSLConfigurationElement {

    private final String contributorName;
    private final HashMap<String, String> attributes;

    public PluginDSLConfigurationElement(IConfigurationElement confEl) {
	contributorName = confEl.getContributor().getName();
	String[] attributeNames = confEl.getAttributeNames();
	attributes = new HashMap<String, String>();
	for (int i = 0; i < attributeNames.length; i++) {
	    String key = attributeNames[i];
	    attributes.put(key, confEl.getAttribute(key));
	}
    }

    @Override
    public DSLContributor getContributor() {
	return new PluginDSLContributor(contributorName);
    }

    @Override
    public String getAttribute(DSLDefinitionsAttribute attrName) {
	String attribute = attributes.get(attrName.value);
	return attribute;
    }

}
