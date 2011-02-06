package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
	 
new DSLInvoker(SimpleSqlDSL.class).eval() {

	SELECT id, age, haircolor FROM students
	
	SELECT id, age, haircolor FROM students WHERE "id!=0" AND "age>20"
}