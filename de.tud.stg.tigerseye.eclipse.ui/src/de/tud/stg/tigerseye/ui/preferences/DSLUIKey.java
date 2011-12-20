package de.tud.stg.tigerseye.ui.preferences;

import java.awt.Color;
import java.util.Random;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;

public abstract class DSLUIKey extends DSLKey<Object> {

    public static final DSLColorKey COLOR = new DSLColorKey();

    protected DSLUIKey(String suffix) {
	super(suffix);
    }

    private static final Random random = new Random();

    /**
     * Returns a random default color.
     * 
     * @param store
     * @return the default color
     */
    public static RGB getDefaultColor(IPreferenceStore store) {
	Color[] cs = TigerseyeUIPreferenceConstants.DEFAULT_COLORS;
	Color c = cs[random.nextInt(cs.length)];
	RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
	return rgb;
	// PreferenceConverter.getDefaultColor(store,TigerseyeUIPreferenceConstants.DEFAULT_COLOR_VALUE)
    }

    /**
     * Returns a random default color. And sets it as the new DSL Color.
     * 
     * @param store
     * @return the default color
     */
    public static RGB getAndSetDefaultColor(IPreferenceStore store, DSLDefinition dsl) {
	RGB defaultColor = getDefaultColor(store);
	dsl.setValue(DSLUIKey.COLOR, defaultColor);
	return defaultColor;
    }

    public static boolean isTigerseyeHighlightingActive(IPreferenceStore store) {
	return store
		.getBoolean(TigerseyeUIPreferenceConstants.TIGERSEYE_EDITOR_HIGHLIGHT_KEYWORDS_ENABLED);
    }

    public static final class DSLColorKey extends DSLKey<RGB> {

	private DSLColorKey() {
	    super(TigerseyeUIPreferenceConstants.DSL_COLOR_ATTRIBUTE);
	}

	@Override
	public RGB getValue(DSLDefinition dsl, IPreferenceStore store)
		throws NoLegalPropertyFoundException {
	    String key = key(dsl);
	    boolean contains = store.contains(key);
	    if (!contains)
		throw new NoLegalPropertyFoundException("No value for " + key
			+ " found.");

	    RGB rgb = PreferenceConverter.getColor(store, key);
	    return rgb;
	}

	@Override
	public void setValue(DSLDefinition dsl, IPreferenceStore store,
		RGB value) {
	    PreferenceConverter.setValue(store, key(dsl), value);
	}

	@Override
	public RGB getDefault(DSLDefinition dsl, IPreferenceStore store) {
	    return getDefaultColor(store);
	}

    }

}
