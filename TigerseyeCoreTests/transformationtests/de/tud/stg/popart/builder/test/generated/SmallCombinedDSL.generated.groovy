package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MapDSL.Entry;
import de.tud.stg.popart.builder.test.dsls.MathDSL;

DSLInvoker.eval([MapDSL.class, MathDSL.class] as Class[]) {
	int a = DSLInvoker.getDSL(
MathDSL.class).sum__p0(
[
1,
2
] as int[])
	int b = DSLInvoker.getDSL(
MathDSL.class).sum__p0(
[
2,
3
] as int[])
	
	println a
	println b
	
	Map m = DSLInvoker.getDSL(
MapDSL.class).buildMap(
Integer.class,
String.class,
[
DSLInvoker.getDSL(
MapDSL.class).buildEntry(
[
DSLInvoker.getDSL(
MapDSL.class).buildEntry(
a,
"Hans"),
b
] as Entry[],
"Peter")
] as Entry[])
	
	print m
}