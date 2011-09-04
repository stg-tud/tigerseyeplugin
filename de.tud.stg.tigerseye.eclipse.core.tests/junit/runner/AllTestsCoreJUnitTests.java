package runner;

import learningtests.fileaccess.ReadingEncodingdependendUnicode;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import utilities.tests.SystemPropertyRuleTest;
import de.tud.stg.popart.builder.transformers.FileTypeTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilderTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTableTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ClassDSLInformationTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodDSLInformationTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionElementTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionScannerTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ParameterDSLInformationTest;
import de.tud.stg.tigerseye.eclipse.core.preferences.TransformerTableDialogTest;
import de.tud.stg.tigerseye.eclipse.core.runtime.FileHelperTest;
import de.tud.stg.tigerseye.eclipse.core.runtime.JDTClasspathResolverTest;
import de.tud.stg.tigerseye.eclipse.core.utils.DSLExtensionsExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.utils.KeyWordsExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.utils.ListBuilderTest;
import de.tud.stg.tigerseye.eclipse.core.utils.ListMapTest;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandlerTest;

@RunWith(Suite.class)
@SuiteClasses({ FileTypeTest.class, //
		TransformerTableDialogTest.class, //
		DSLExtensionsExtractorTest.class, //
		KeyWordsExtractorTest.class, //
		OutputPathHandlerTest.class, //
		FileHelperTest.class, //
		JDTClasspathResolverTest.class, //
		UnicodeLookupTableTest.class, //
		GrammarBuilderTest.class, //
		GrammarTest.class, //
		ListBuilderTest.class, //
		ListMapTest.class, //
		ClassDSLInformationTest.class,//
		MethodDSLInformationTest.class,//
		MethodProductionScannerTest.class,//
		ParameterDSLInformationTest.class,//
		MethodProductionScannerTest.class,//
		MethodProductionElementTest.class,//
		SystemPropertyRuleTest.class, //
		ReadingEncodingdependendUnicode.class, //
})
public class AllTestsCoreJUnitTests {

}
