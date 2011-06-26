package de.tud.stg.tigerseye.examples.statemachine;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.DSLTransformationTestBase;


import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.examples.statemachine.StateMachineDSL;
import de.tud.stg.tigerseye.test.CodePrinterFactory;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;
import de.tud.stg.tigerseye.test.TestUtils;
import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;

public class StateMachineDSLTest extends DSLTransformationTestBase {

	
	private static final Logger logger = LoggerFactory
			.getLogger(StateMachineDSLTest.class);
	private DefaultDSLTransformationTester tester;
	
	

	@Test
	public void shouldNotTransformAnything() throws Exception {
		assertTransformedDSLEqualsExpected(
				"StateMachineDSL",
				StateMachineDSL.class);
	}	
	
	@Test
	public void shouldTransformEverythingInCombination() throws Exception {		
		assertTransformedDSLEqualsExpected("StateMachineDSLSupportsRARRSyntax", StateMachineDSL.class);
	}

}
