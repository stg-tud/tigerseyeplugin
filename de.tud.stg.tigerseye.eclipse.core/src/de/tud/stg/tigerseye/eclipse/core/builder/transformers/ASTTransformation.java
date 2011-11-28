package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Set;

import aterm.ATerm;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;

/**
 * The is the interface for all AST transformations that are processed after
 * parsing on the generated syntax tree
 * 
 * @author Kamil Erhard
 * 
 */
public interface ASTTransformation extends Transformation {

    public ATerm transform(Context context, ATerm aterm);

    // XXX(Leo_Roos;Nov 22, 2011) currentyl ignored maybe remove?
    public Set<ATerm> getRequirements();

    // XXX(Leo_Roos;Nov 22, 2011) currentyl ignored maybe remove?
    public Set<ATerm> getAssurances();
}
