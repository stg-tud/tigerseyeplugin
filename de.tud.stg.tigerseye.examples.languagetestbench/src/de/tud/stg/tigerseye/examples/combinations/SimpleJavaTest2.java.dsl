package de.tud.stg.tigerseye.examples.combinations;
import java.util.Set;

import de.tud.stg.popart.builder.eclipse.EDSL;

@EDSL({"set", "sql"})
public class SimpleJavaTest2 {
	
	public SimpleJavaTest2() {
	
	}
	
	public void foo() { 
		
		Set s = {"k", "l"} ⋃ {"m", "n"};
		
		System.out.println(s);
		
		SELECT "id" FROM "students";
	}
	
	public static void main(String[] args) {
		new SimpleJavaTest2().foo();
	}
}
