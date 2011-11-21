package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources;

import java.util.Set;

import de.tud.stg.popart.builder.test.dsls.SetDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
import de.tud.stg.tigerseye.dslsupport.DSLInvoker;

public class CombinedDSLInJavaFile {
	
	public CombinedDSLInJavaFile() {
	
	}
	
	public void foo() { 
		
		Set s = {"k", "l"} ⋃ {"m", "n"};
		
		System.out.println("Should return \"[n, l, m, k]\" was: " + s);
		
		String query = SELECT "id" FROM "students";
		
		System.out.println("Query was: " + query);
	}
	
	public static void main(String[] args) {
		new CombinedDSLInJavaFile().foo();
	}
}