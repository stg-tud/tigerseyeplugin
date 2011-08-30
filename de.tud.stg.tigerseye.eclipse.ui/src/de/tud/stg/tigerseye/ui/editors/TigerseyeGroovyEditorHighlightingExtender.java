package de.tud.stg.tigerseye.ui.editors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.eclipse.editor.GroovyTagScanner;
import org.codehaus.groovy.eclipse.editor.highlighting.IHighlightingExtender;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.utils.KeyWordExtractor;
import de.tud.stg.tigerseye.ui.preferences.DSLUIKey;

public class TigerseyeGroovyEditorHighlightingExtender implements
	IHighlightingExtender {

    private final ColorManager cmanager = new ColorManager();

    private final HashMap<DSLDefinition, Token> tokenMap;

    public TigerseyeGroovyEditorHighlightingExtender() {
	this.tokenMap = new HashMap<DSLDefinition, Token>();
	TigerseyeCore.getPreferences().addPropertyChangeListener(
		new IPropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			// color changing without closing the editor
			String property = event.getProperty();
			Set<DSLDefinition> keySet = tokenMap.keySet();
			for (DSLDefinition dsl : keySet) {
			    String colorKey = dsl.getKeyFor(DSLUIKey.COLOR);
			    boolean isColorForThisLang = colorKey
				    .equals(property);
			    if (isColorForThisLang) {
				tokenMap.get(dsl).setData(getTextAttributeFor(dsl));
			    }
			}
		    }
		});
    }

    @Override
    public List<String> getAdditionalGroovyKeywords() {
	/* No additional keywords to color like Groovy keywords. */
	return null;
    }

    @Override
    public List<String> getAdditionalGJDKKeywords() {
	/* No additional keywords to color like Groovy JDK keywords. */
	return null;
    }

    @Override
    public List<IRule> getAdditionalRules() {
	List<IRule> additionalRules = new ArrayList<IRule>();
	IPreferenceStore store = TigerseyeCore.getPreferences();
	if (!DSLUIKey.isTigerseyeHighlightingActive(store))
	    return additionalRules;

	for (DSLDefinition dsl : getActiveDSLs()) {
	    Token token = getTokenFor(dsl);
	    WordRule keywordsRule = new DSLWordRule(new TigerWordDetector(),
		    dsl);
	    for (String keyWord : getMethodKeyWordsForDSL(dsl)) {
		keywordsRule.addWord(keyWord, token);
	    }
	    tokenMap.put(dsl, token);
	    additionalRules.add(keywordsRule);
	}
	return additionalRules;
    }

    // FIXME refactor keywordextractor: so that it takes class in constructor
    // and provides different methods working on that class
    private List<String> getMethodKeyWordsForDSL(DSLDefinition dsl) {
	List<String> keywords = new ArrayList<String>();
	KeyWordExtractor keyWordExtractor = new KeyWordExtractor(
		dsl.getDSLClassChecked());
	Method[] methodKeywords = keyWordExtractor.getMethodKeywords();
	for (Method method : methodKeywords) {
	    keywords.add(method.getName());
	}

	Field[] declaredLiteralKeywords = keyWordExtractor
		.getDeclaredLiteralKeywords();
	for (Field field : declaredLiteralKeywords) {
	    keywords.add(field.getName());
	}

	return keywords; // TODO all chained keywords to list (such as
			 // selectFormWhere --> select, from, where)
    }

    private List<DSLDefinition> getActiveDSLs() {
	List<DSLDefinition> activeDSLs = new ArrayList<DSLDefinition>();
	Collection<DSLDefinition> dslDefinitions = TigerseyeCore
		.getLanguageProvider().getDSLDefinitions();
	for (DSLDefinition dsl : dslDefinitions) {
	    if (dsl.isActive()) {
		activeDSLs.add(dsl);
	    }
	}
	return activeDSLs;
    }

    private Token getTokenFor(DSLDefinition dsl) {
	return new Token(getTextAttributeFor(dsl));
    }

    private TextAttribute getTextAttributeFor(DSLDefinition dsl) {
	RGB value;
	try {
	    value = dsl.getValue(DSLUIKey.COLOR);
	} catch (NoLegalPropertyFoundException e) {
	    value = DSLUIKey.getDefaultColor(TigerseyeCore.getPreferences());
	}
	return new TextAttribute(cmanager.getColor(value), null, SWT.BOLD);
    }

    /**
     * Marker extension for identification purposes via {@code instanceof}.
     * Since {@link GroovyTagScanner} uses
     * {@link org.eclipse.jdt.internal.ui.text.CombinedWordRule} this class can
     * be used as container to access the defined words and tokens.
     * 
     * @author Leo Roos
     * 
     */
    public class DSLWordRule extends WordRule {

	private final DSLDefinition dsl;
	private final HashMap<String, IToken> wordConfiguration;

	public DSLWordRule(IWordDetector detector, DSLDefinition dsl) {
	    super(detector);
	    this.dsl = dsl;
	    this.wordConfiguration = new HashMap<String, IToken>();
	}

	public DSLDefinition getDsl() {
	    return dsl;
	}

	@Override
	public void addWord(String word, IToken token) {
	    super.addWord(word, token);
	    this.wordConfiguration.put(word, token);
	}

	/**
	 * @return the a Map of words and their associated tokens.
	 */
	public HashMap<String, IToken> getWordConfiguration() {
	    return wordConfiguration;
	}

    }

    public static class TigerWordDetector implements IWordDetector {

	@Override
	public boolean isWordStart(char c) {
	    return Character.isJavaIdentifierPart(c);
	}

	@Override
	public boolean isWordPart(char c) {
	    return Character.isJavaIdentifierPart(c);
	}

    }

}
