package de.tud.stg.popart.builder.transformers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.builder.core.GrammarBuilder;
import de.tud.stg.popart.dslsupport.DSL;

/**
 * This class represents the context during the transformation phase.
 * 
 * @author Kamil Erhard
 * 
 */
public class Context {
	public Map<String, Class<? extends DSL>> dslClasses = new HashMap<String, Class<? extends DSL>>();
	public Set<String> currentAssurances = new HashSet<String>();

	public GrammarBuilder grammarBuilder;

	private Filetype filetype;

	public Filetype getFiletype() {
		return this.filetype;
	}

	public void setFiletype(Filetype filetype) {
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

	public Class<? extends DSL>[] getDSLClasses() {
		return this.dslClasses.values().toArray(
				new Class[this.dslClasses.size()]);
	}

	public GrammarBuilder getGrammarBuilder() {
		return this.grammarBuilder;
	}

	public void setGrammarBuilder(GrammarBuilder grammarBuilder) {
		this.grammarBuilder = grammarBuilder;
	}

	public String getFileName() {
		return this.fileName;
	}

	public Set<String> getCurrentAssurances() {
		return this.currentAssurances;
	}
}
