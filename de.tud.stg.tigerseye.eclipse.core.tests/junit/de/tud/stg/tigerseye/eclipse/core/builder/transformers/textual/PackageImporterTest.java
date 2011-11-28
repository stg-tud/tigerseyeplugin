package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;


import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SetDSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;

@SuppressWarnings("rawtypes")
public class PackageImporterTest {

	private Map mtmap = new HashMap();
	
	private PackageImporter pi;

	@Before
	public void setUp() throws Exception {
		pi = new PackageImporter();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldContainAddedDSLs() throws Exception {
		Context context = createContextMock(MathDSL.class, SetDSL.class);

		String ret = pi.transform(context, "", mtmap);
		assertThat(ret).containsAllSubstrings(MathDSL.class.getCanonicalName(), SetDSL.class.getCanonicalName());
	}
	
	@Test
	public void shouldNotTransformCodeWithSemicolon() throws Exception {
		Context context = createContextMock(MathDSL.class);
		
		String packagePart = "package de.tud.stg.tigerseye.examples.set\n";
		String codePart = "set(name:'SemicolonTest'){\n"+"        println \"hello;world\"\n" + 
		"        println \"hello;world\"\n" + 
		"}";
		String input = packagePart + 
				"\n" + 
				codePart 
				;
		String ret = pi.transform(context, input, new HashMap());
		//should not touch code part
		assertThat(ret).contains(codePart);
	}
	
	@Test
	public void shouldInsertAfterPackageDeclaration() throws Exception {
		String somecomments = "/*some comments*/\n";
		String packagedeclr = "package asdf.d234.d$asdfasl;";
		String randomCode = " ... Code.asdf\n\n;;.ml.-kdsy879676uz83434asdf";
		String input = somecomments + packagedeclr  + randomCode;
		Context createContextMock = createContextMock(MathDSL.class);
		String ret = pi.transform(createContextMock, input, mtmap);
		assertThat(ret).startsWith(somecomments+packagedeclr);
		assertThat(ret).contains(MathDSL.class.getCanonicalName());
		assertThat(ret).contains(randomCode);
	}

	private Context createContextMock(final Class ... classes) {
		 Context c = mock(Context.class);
		 when(c.getDSLClasses()).thenReturn(classes);
		return c;
	}

}
