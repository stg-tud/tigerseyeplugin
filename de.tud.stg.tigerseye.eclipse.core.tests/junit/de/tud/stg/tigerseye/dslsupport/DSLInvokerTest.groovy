package de.tud.stg.tigerseye.dslsupport;

import static org.junit.Assert.*;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;

public class DSLInvokerTest {


	
	@Test
	public void shouldThrowExceptionIfOnlyClosure() throws Exception {
		def res = DSLInvoker.eval(MathDSL.class, {
			def p = sum__p0(
					[10, 10]as int[])
		})
		//Result is last from closure
		assertEquals(20,res)
	}

	@Test
	public void shouldTransformSimpleDSLStatement() throws Exception {
		def res = DSLInvoker.eval(MathDSL.class, {
			sum__p0(
					[10, 10]as int[])
		})
		//Result is last from closure
		assertEquals(20,res)
	}

	@Test
	public void shouldTransformCombination() throws Exception {
		int spy = 0;
		def res = DSLInvoker.eval([MathDSL.class, SimpleSqlDSL.class],{
			spy = sum__p0(
					[10, 10]as int[])
			selectFrom(
					[
						"id",
						"age",
						"haircolor"
					]
					as String[],
					["students"
					]as String[])
		})
		//Last statement was selectFrom which returns the generated Query
		assertThat(res).contains("id");
		assertThat(res).contains("age");
		assertThat(res).contains("haircolor");
		assertThat(res).contains("students");
		assertEquals(20,spy) 
	}
	
	@Test
	public void shouldEvalClosureWithoutSpecialSyntax() throws Exception {
		int i = DSLInvoker.eval(MathDSL.class) {   def i = 1 + 1  }
		assertEquals(2, i)
	}
	
	@Ignore("Does not tell anything about Tigerseye")
	@Test
	public void shouldFailIfRunAsGroovyScript() throws Exception {
		//Expected test to fail because that File run as a Groovy Script via context menu will cause a MissingMethodException
		GroovyScriptEngine gse = new GroovyScriptEngine(".");
		File script = new File("junit/"+ RunAsGroovyScriptFails.class.getName().replaceAll("\\.", "/") + ".groovy")
		String form = script.toString()
		String res = gse.run(form, null)
		assertThat(res).contains("why you no run as groovy script?");
	}
}
