package de.tud.stg.tigerseye.eclipse.core.builder.transformers;


/**
 * A description of transformation priorities, which determine the point during
 * the build when the transformation is applied.
 * <p>
 * The build is divided in multiple phases
 * <ul>
 * <li/>before the Earley transformation, the source presentation is text
 * <li/>after the Earley transformation, the source presentation is an AST
 * <li/>after the AST transforamtions the source presentation is text
 * </ul>
 * 
 * Accordingly before the Earley transformation and after the AST
 * transformations only the {@link TextualTransformation}s can be applied and in
 * between the {@link ASTTransformation}.
 * 
 * @author Leo Roos
 * 
 */
public interface TransformationConstants {

    int PURIFICATION_EXTRACTION_TRANSFORMATION = 1000;

    int KEYWORD_TRANSLATION_TRANSFORMATION = 2000;

    /**
     * All transformations with a lesser priority are expected to be Textual
     * Transformers
     * <p>
     * Transformers of this priority will be ignored
     */
    int AFTER_EARLEY_TRANSFORMATION = 10000;

    int KEYWORD_CHAINING_TRANSFORMATION = 11000;

    int INVOKATION_TRANSFORMATION = 12000;

    int CLOSURE_RESULT_TRANSFORMATION = 10000;

    /**
     * All transformations greater this priority are expected to be Textual
     * Transformers
     * <p>
     * Transformers of this priority will be ignored
     */
    int AFTER_AST_TRANSFORMATION = 20000;

    int BOOT_STRAP_TRANSFORMATION = 21000;

    int PURIFICATION_REINSERTION_TRANSFORMATION = 22000;

    /**
     * Tries to determine which dependencies have accumulated and tries to
     * import them. Should be executed as last transformation since it doesn't
     * transform anything, but introduces new strings which might easily be
     * misinterpreted.
     */
    int PACKAGE_IMPORTER_TRANSFORMATION = 23000;

}
