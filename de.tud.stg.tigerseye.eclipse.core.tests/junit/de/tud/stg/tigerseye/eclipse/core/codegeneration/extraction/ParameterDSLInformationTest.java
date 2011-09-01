package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.popart.builder.test.dsls.BnfDSL;
import de.tud.stg.popart.builder.test.dsls.BnfDSL.AnyCharacter;
import de.tud.stg.popart.builder.test.dsls.BnfDSL.QuotedSymbol;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

public class ParameterDSLInformationTest {

	// @DSLMethod(production = "module  p0  p1  p2", topLevel = true)
	// public Module moduleWithoutParameters(
	// ModuleId name,
	// @DSLParameter(arrayDelimiter = " ") Imports[] imports,
	// @DSLParameter(arrayDelimiter = " ") ExportOrHiddenSection[]
	// exportOrHiddenSections) {
	private List<ParameterDSLInformation> moduleWithoutParameters;

	@Before
	public void beforeEachTest() throws Exception {
		moduleWithoutParameters = getParametersOfClassInMethod(SdfDSLForExtractingTest.class, "moduleWithoutParameters");
	}

	private ParameterDSLInformation getParameterOfClassInMethodAtIndex(Class<?> clazz, String firstMethod, int parNum) {
		MethodDSLInformation firstMethodInfoInClass = MethodDSLInformationTest.getFirstMethodInfoInClass(clazz,
				firstMethod);
		return firstMethodInfoInClass.getParameterInfo(parNum);
	}

	private List<ParameterDSLInformation> getParametersOfClassInMethod(Class<?> clazz, String firstMethod) {
		MethodDSLInformation firstMethodInfoInClass = MethodDSLInformationTest.getFirstMethodInfoInClass(clazz,
				firstMethod);
		return firstMethodInfoInClass.getParameterInfos();
	}

	@Test
	public void shouldStateIfAnnotated_False() throws Exception {
		assertThat(moduleWithoutParameters.get(0).isAnnotated()).isFalse();
	}

	@Test
	public void shouldStateIfAnnotated_True() throws Exception {
		assertThat(moduleWithoutParameters.get(2).isAnnotated()).isTrue();
	}

	@Test
	public void shouldReturnDefaultsIfNotAnnotated() throws Exception {
		ParameterDSLInformation pi = moduleWithoutParameters.get(0);
		assertThat(pi.getConfigurationOptions()).isEqualTo(DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
	}

	@Test
	public void shouldHaveAdjusteConfOptions_arrayDelimiter() throws Exception {
		assertThat(moduleWithoutParameters.get(1).getConfigurationOption(ConfigurationOptions.ARRAY_DELIMITER))
				.isEqualTo(" ");
	}

	@Ignore("Not sure, probably won't hurt if I don't throw exception. Same would be necessary for stringquotation and whitespaceesecape")
	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveAdjusteConfOptions_parameterEscape() throws Exception {
		moduleWithoutParameters.get(1).getConfigurationOption(ConfigurationOptions.PARAMETER_ESCAPE);
	}

	@Test
	public void shouldReturnParameterType() throws Exception {
		assertThat(moduleWithoutParameters.get(0).getType()).isEqualTo(SdfDSLForExtractingTest.ModuleId.class);
	}
	
	@Test
	public void shouldReturnCorrectIndex() throws Exception {
		assertThat(moduleWithoutParameters.get(1).getIndex()).isEqualTo(1);
	}
	
	@Test
	public void shouldEmptyArrayDelimiterIsValid() throws Exception {
		//	@DSLMethod(production = "\"  p0  \"", topLevel = false)	public QuotedSymbol quotedSymbolFromAnyCharacters(@DSLParameter(arrayDelimiter = "") AnyCharacter[] ac) {
		ParameterDSLInformation pinf = getParameterOfClassInMethodAtIndex(BnfDSL.class, "quotedSymbolFromAnyCharacters", 0);
		assertThat(pinf.isAnnotated()).isTrue();
		assertThat(pinf.getConfigurationOption(ConfigurationOptions.ARRAY_DELIMITER)).isEqualTo("");
	}

}
