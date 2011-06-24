package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFound;

/**
 * This class represents the context of a Tigerseye source file during the
 * transformation phase.
 * 
 * @author Kamil Erhard
 * 
 */
public class Context {
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

    public Context(String fileName) {
	this.fileName = fileName;
    }

    public void addDSL(String extension, Class<? extends DSL> clazz) {
	this.dslClasses.put(extension, clazz);
    }

    public String[] getDSLExtensions() {
	return this.dslClasses.keySet().toArray(
		new String[this.dslClasses.size()]);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public Class<? extends DSL>[] getDSLClasses() {
	return this.dslClasses.values().toArray(
		new Class[this.dslClasses.size()]);
    }

    public String getFileName() {
	return this.fileName;
    }

    public Set<String> getCurrentAssurances() {
	return this.currentAssurances;
    }

    public void addDSL(DSLDefinition dsl) throws NoLegalPropertyFound {
	addDSL(dsl.getValue(DSLKey.EXTENSION), dsl.loadClass());
	this.dsls.add(dsl);
    }

    public List<DSLDefinition> getDsls() {
	/*
	 * To stay compatible with older code but nevertheless ensuring a
	 * consistent use
	 */
	if (this.dslClasses.size() != dsls.size())
	    throw new IllegalStateException(
		    "Tried to access Context in an inconsistent manner. Defined amound of DSL classes is unequal to the amount of defined dsls.");

	return Collections.unmodifiableList(dsls);
    }

}
