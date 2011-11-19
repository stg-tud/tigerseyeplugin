package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Map;
import java.util.Set;

import aterm.ATerm;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;

/**
 * The is the interface for all AST transformations that are processed after
 * parsing on the generated syntax tree
 * 
 * @author Kamil Erhard
 * 
 */
public interface ASTTransformation extends Transformation {
    public ATerm transform(Map<String, DSLMethodDescription> moptions, ATerm aterm);

	public Set<ATerm> getRequirements();

	public Set<ATerm> getAssurances();
}
