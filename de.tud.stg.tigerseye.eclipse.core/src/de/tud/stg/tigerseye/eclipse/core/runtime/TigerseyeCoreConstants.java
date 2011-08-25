package de.tud.stg.tigerseye.eclipse.core.runtime;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCoreActivator;

public class TigerseyeCoreConstants {

    public static final String TIGERSEYE_NATURE_ID = TigerseyeCoreActivator.PLUGIN_ID
	    + ".tigerseyeNature";

    public static final String DSLDEFINITIONS_EXTENSION_POINT_ID = "de.tud.stg.tigerseye.dslDefinitions";
    public static final String DSLDEFINITIONS_LANGUAGE_ELEMENT = "language";

    /**
     * The dslDefinitions attributes.
     */
    private static final String DSLDEFINITIONS_EXTENSION_ATTRIBUTE = "extension";
    private static final String DSLDEFINITIONS_CLASS_ATTRIBUTE = "class";
    private static final String DSLDEFINITIONS_NAME_ATTRIBUTE = "name";

    /**
     * Represents the valid attributes of the language element of the
     * dslDefinitions extension point.
     * 
     * @author Leo Roos
     * 
     */
    public enum DSLDefinitionsAttribute {
	Extension(DSLDEFINITIONS_EXTENSION_ATTRIBUTE), Class(
		DSLDEFINITIONS_CLASS_ATTRIBUTE), PrettyName(
		DSLDEFINITIONS_NAME_ATTRIBUTE);

	public final String value;

	private DSLDefinitionsAttribute(String value) {
	    this.value = value;
	}
    }

}
