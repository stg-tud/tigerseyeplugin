package de.stg.tud.tigerseye.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.NoLegalPropertyFound;

public abstract class DSLUIKey extends DSLKey<Object> {

    /**
     * Key to access the color property of a DSL
     */
    public static final DSLColorKey COLOR = new DSLColorKey();

    protected DSLUIKey(String suffix) {
	super(suffix);
    }

    /**
     * All DSLs have the same default color.
     * 
     * @param store
     * @return the default color for DSLs.
     */
    public static RGB getDefaultColor(IPreferenceStore store) {
	return PreferenceConverter.getDefaultColor(store,
		TigerseyeUIPreferenceConstants.DEFAULT_COLOR_VALUE);
    }

    public static final class DSLColorKey extends DSLKey<RGB> {



	protected DSLColorKey() {
	    super(TigerseyeUIPreferenceConstants.DSL_COLOR_ATTRIBUTE);
	}

	@Override
	public RGB getValue(DSLDefinition dsl, IPreferenceStore store)
		throws NoLegalPropertyFound {
	    RGB rgb = PreferenceConverter.getColor(store, key(dsl));
	    return rgb;
	}

	@Override
	public void setValue(DSLDefinition dsl, IPreferenceStore store,
		RGB value) {
	    PreferenceConverter.setValue(store, key(dsl), value);
	}

	/**
	 * Forwards to the static method
	 * {@link DSLUIKey#getDefaultColor(IPreferenceStore)}.
	 * 
	 * @see {@link DSLUIKey#getDefaultColor(IPreferenceStore)}
	 */
	@Override
	public RGB getDefault(DSLDefinition dsl, IPreferenceStore store) {
	    return getDefaultColor(store);
	}

    }

}
