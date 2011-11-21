package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static de.tud.stg.tigerseye.test.TransformationUtils.dslSingle;
import static de.tud.stg.tigerseye.test.TransformationUtils.dslsList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.SetDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources.SetDSLForInvokationsTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyJavaCodePrinter;
import de.tud.stg.tigerseye.test.TestDSLTransformation;

public class InvokationDispatcherJavaTransformationTest {

	@Test
	public void shouldTransformSetDSLWithDSLInvoke() throws Exception {
		 Class<? extends DSL> clazz = SetDSLForInvokationsTransformation.class;
		String input = "Set s = {\"k\", \"l\"} ⋃ {\"m\", \"n\"};\n"; 
		
		String expected = "Set s = DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).union(\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).asSet(\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).multiElementedList(\n" + 
				"\"k\",\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).singleElementedList(\n" + 
				"\"l\"))),\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).asSet(\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).multiElementedList(\n" + 
				"\"m\",\n" + 
				"DSLInvoker.getDSL(\n" + 
				"SetDSLForInvokationsTransformation.class).singleElementedList(\n" + 
				"\"n\"))));";

		String output = performTransformation(input, dslSingle(clazz));
		assertThat(output).isEqualToIgnoringWhitespace(expected);
	}
	
	
	@Test
	public void shouldTransformSQLDSLWithDSLInvoke() throws Exception {
		 Class<? extends DSL> clazz = SimpleSqlDSL.class;
		String input = "SELECT \"id\" FROM \"students\";"; 
		String output = performTransformation(input,dslSingle(clazz));
		String expected = "DSLInvoker.getDSL(SimpleSqlDSL.class).selectFrom(new String[] {\"id\"},new String[] {\"students\"});";
		assertThat(output).isEqualToIgnoringWhitespace(expected);
	}
	
	@Test
	public void shouldTransformCombinationOfSetAndSQL() throws IOException, Exception {
		List<Class<? extends DSL>> dslclasses = dslsList(SimpleSqlDSL.class).add(SetDSL.class).toList();
		String input = getResource("CombinedDSLInJavaFile.java.dsl");
		String transformed = performTransformation(input, dslclasses);
		String expected = getResource("CombinedDSLInJavaFile.java.expected");
		assertThat(transformed).isEqualToIgnoringWhitespace(expected);
	}


	private String performTransformation(String input, List<Class<? extends DSL>> clazzes) throws VisitFailure {
		return TestDSLTransformation.performCustomTransformation(clazzes, input, new PrettyJavaCodePrinter(), new InvokationDispatcherJavaTransformation());
	}

	private String getResource(String resourceFile) throws IOException {
		InputStream inputStream = getClass().getResourceAsStream("resources/" + resourceFile);
		return IOUtils.toString(inputStream);
	}

}
