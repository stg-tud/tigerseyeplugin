package de.tud.stg.popart.builder.transformers;

import java.util.Set;

import aterm.ATerm;

/**
 * The interface for all AST transformations that are processed after parsing on the generated syntrax tree
 * 
 * @author Kamil Erhard
 * 
 */
public interface ASTTransformation extends Transformation {
	public ATerm transform(Context context, ATerm aterm);

	public Set<ATerm> getRequirements();

	public Set<ATerm> getAssurances();
}
