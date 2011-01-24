package de.tud.stg.tigerseye.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.DSLKey;
import de.tud.stg.tigerseye.core.NoLegalPropertyFound;

public abstract class DSLUIKey extends DSLKey<Object> {

    public static final DSLColorKey COLOR = new DSLColorKey();

    protected DSLUIKey(String suffix) {
	super(suffix);
    }

    public static RGB getDefaultColor(IPreferenceStore store) {
	return PreferenceConverter.getDefaultColor(store,
		TigerseyeUIPreferenceConstants.DEFAULT_COLOR_VALUE);
    }

    public static final class DSLColorKey extends DSLKey<RGB> {

	private DSLColorKey() {
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

	@Override
	public RGB getDefault(DSLDefinition dsl, IPreferenceStore store) {
	    return PreferenceConverter.getDefaultColor(store, key(dsl));
	}

    }

}
