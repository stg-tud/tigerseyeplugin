package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
	 
new DSLInvoker(ConditionalDSL.class).eval() {

	boolean b = 1==0;
	
	ifThenElse(
b,
{
print "is true"
		}
,
{
print "is false"
		}
)
}