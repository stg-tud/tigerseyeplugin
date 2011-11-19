package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;

/**
 * This class represents the context of a Tigerseye source file during the
 * transformation phase.
 * 
 * @author Kamil Erhard
 * 
 */
public class Context {

    private static final Logger logger = LoggerFactory.getLogger(Context.class);
    private final List<DSLDefinition> dsls = new ArrayList<DSLDefinition>();
    private final Map<String, Class<? extends DSL>> dslClasses = new HashMap<String, Class<? extends DSL>>();
    private final Set<String> currentAssurances = new HashSet<String>();

    public Class<? extends DSL> getDSLForExtension(String dslExtension) {
	return dslClasses.get(dslExtension);
    }

    private FileType filetype;

    public FileType getFiletype() {
	return this.filetype;
    }

    public void setFiletype(FileType filetype) {
	this.filetype = filetype;
    }

    private final String fileName;
    private IFile transformedFile;

    public Context(String fileName) {
	this.fileName = fileName;
    }

    public void addDSL(String extension, Class<? extends DSL> clazz) {
	this.dslClasses.put(extension, clazz);
    }

    public String[] getDSLExtensions() {
	return this.dslClasses.keySet().toArray(new String[this.dslClasses.size()]);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public Class<? extends DSL>[] getDSLClasses() {
	return this.dslClasses.values().toArray(new Class[this.dslClasses.size()]);
    }

    public String getFileName() {
	return this.fileName;
    }

    public Set<String> getCurrentAssurances() {
	return this.currentAssurances;
    }

    public void addDSL(DSLDefinition dsl) throws NoLegalPropertyFoundException {
	if (dsl.isDSLClassLoadable()) {
	    addDSL(dsl.getValue(DSLKey.EXTENSION), dsl.getDSLClassChecked());
	    this.dsls.add(dsl);
	} else {
	    logger.error("tried to add not loadable dsl {}", dsl);
	}
    }

    public List<DSLDefinition> getDsls() {
	/*
	 * To stay compatible with older code but nevertheless ensuring a
	 * consistent use
	 */
	if (this.dslClasses.size() != dsls.size())
	    throw new IllegalStateException(
		    "Tried to access Context in an inconsistent manner. Defined amount of DSL classes is unequal to the amount of defined dsls.");

	return Collections.unmodifiableList(dsls);
    }

    /**
     * This method will simply call {@link #addDSL(String, Class)} for each
     * element and ignore any element that causes an exception during the
     * addition.
     * 
     * @param dslDefinitions
     */
    public void addDSLs(Set<DSLDefinition> dslDefinitions) {
	for (DSLDefinition dsl : dslDefinitions) {
	    try {
		this.addDSL(dsl);
	    } catch (NoLegalPropertyFoundException e) {
		logger.warn("DSL {} not properly initialized. Will be ignored.", dsl, e);
	    }
	}
    }

    public void setTransformedFile(IFile srcFile) {
	this.transformedFile = srcFile;
    }

    public IFile getTransformedFile() {
	return transformedFile;
    }

    public IProject getProject() {
	if (transformedFile == null)
	    throw new IllegalStateException("transformedFile has not been set yet.");
	return transformedFile.getProject();
    }

}
