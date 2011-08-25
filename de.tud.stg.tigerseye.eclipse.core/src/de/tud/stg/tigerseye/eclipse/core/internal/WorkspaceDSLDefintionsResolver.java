package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

/**
 * Resolves all dslDefinition projects that are currently defined in the
 * workspace.
 * 
 * @author Leo Roos
 * 
 */
public class WorkspaceDSLDefintionsResolver {


    public static class WorkspaceDSL {
	public IPluginElement dslLanguageDefinition;
	public IProject workspaceProject;
	private final HashMap<DSLDefinitionsAttribute, String> attributes = new HashMap<DSLDefinitionsAttribute, String>();

	public void addAttribute(DSLDefinitionsAttribute name, String value) {
	    this.attributes.put(name, value);
	}

	public String getAttribute(DSLDefinitionsAttribute name) {
	    return this.attributes.get(name);
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(WorkspaceDSL.this,
		    ToStringStyle.SIMPLE_STYLE);
	}

	public String getPluginID() {
	    return dslLanguageDefinition.getPluginBase().getId();
	}

    }

    public WorkspaceDSLDefintionsResolver(IPluginModelBase[] workspacePlugins) {
	this.workspacePlugins = workspacePlugins;
    }

    private final Logger logger = LoggerFactory
	    .getLogger(WorkspaceDSLDefintionsResolver.class);
    private final IPluginModelBase[] workspacePlugins;

    public Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> getWorkspaceDSLDefintions() {
	Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> workspaceDSLs = getWorkspaceDSLs();

	for (WorkspaceDSLDefintionsResolver.WorkspaceDSL wdsl : workspaceDSLs) {
	    fillWorkspaceDSLAttributes(wdsl);
	}

	return workspaceDSLs;
    }

    private Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> getWorkspaceDSLs() {

	List<IPluginElement> tigerseyelangplugins = getDSLDefinitionsExtendingWorkspaceProjects();
	HashMap<File, IProject> fileToIProject = getFileToWorkspaceProjects();

	Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> workspaceDSLs = new HashSet<WorkspaceDSLDefintionsResolver.WorkspaceDSL>();
	for (IPluginElement teplugin : tigerseyelangplugins) {

	    WorkspaceDSLDefintionsResolver.WorkspaceDSL wdsl = new WorkspaceDSLDefintionsResolver.WorkspaceDSL();
	    wdsl.dslLanguageDefinition = teplugin;
	    wdsl.workspaceProject = fileToIProject
		    .get(getPluginModelProjectLocationAsFile(teplugin
			    .getPluginModel()));
	    workspaceDSLs.add(wdsl);
	}
	return workspaceDSLs;

    }

    private List<IPluginElement> getDSLDefinitionsExtendingWorkspaceProjects() {
	List<IPluginExtension> dslDefinitions = new ArrayList<IPluginExtension>();
	for (IPluginModelBase wp : workspacePlugins) {
	    IExtensions origExtensions = wp.getExtensions();
	    IPluginExtension[] extensions = origExtensions.getExtensions();
	    for (IPluginExtension ipe : extensions) {
		String point = ipe.getPoint();
		if (TigerseyeCoreConstants.DSLDEFINITIONS_EXTENSION_POINT_ID
			.equals(point)) {
		    dslDefinitions.add(ipe);
		}
	    }
	}

	ArrayList<IPluginElement> languageElements = new ArrayList<IPluginElement>();
	for (IPluginExtension ipe : dslDefinitions) {
	    IPluginObject[] children = ipe.getChildren();
	    for (IPluginObject child : children) {
		IPluginElement pe = (IPluginElement) child;
		if (TigerseyeCoreConstants.DSLDEFINITIONS_LANGUAGE_ELEMENT
			.equals(pe.getName())) {
		    languageElements.add(pe);
		    logger.debug(
			    "for {} added element: {} with attributes {}",
			    new Object[] { pe.getPluginBase().getName(),
				    pe.getName(),
				    ArrayUtils.toString(pe.getAttributes()) });
		}
	    }
	}
	return languageElements;
    }

    private HashMap<File, IProject> getFileToWorkspaceProjects() {
	HashMap<File, IProject> hashMap = new HashMap<File, IProject>();

	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	for (IProject iProject : projects) {
	    hashMap.put(new File(iProject.getLocationURI()), iProject);
	}
	return hashMap;
    }

    private void fillWorkspaceDSLAttributes(
	    WorkspaceDSLDefintionsResolver.WorkspaceDSL wdsl) {
	IPluginElement pe = wdsl.dslLanguageDefinition;
	if (!TigerseyeCoreConstants.DSLDEFINITIONS_LANGUAGE_ELEMENT.equals(pe
		.getName()))
	    throw new IllegalArgumentException(
		    "Expected language element but was " + pe);
	DSLDefinitionsAttribute[] atts = TigerseyeCoreConstants.DSLDefinitionsAttribute
		.values();
	for (DSLDefinitionsAttribute attr : atts) {
	    IPluginAttribute attribute = pe.getAttribute(attr.value);
	    if (attribute != null) {
		wdsl.addAttribute(attr, attribute.getValue());
	    } else
		logger.info("No value for attribute {} found for element {}",
			attr, pe);
	}

    }

    private File getPluginModelProjectLocationAsFile(IPluginModelBase key) {
	String installLoc = key.getInstallLocation();
	File locationURI = new File(installLoc);
	return locationURI;
    }

}
