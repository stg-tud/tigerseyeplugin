package runner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


import de.tud.stg.popart.dslsupport.InterpreterCombinerTest;
import de.tud.stg.tigerseye.examples.logo.LogoDSLTest;
import de.tud.stg.tigerseye.examples.mapdsl.MapDSLTest;
import de.tud.stg.tigerseye.examples.setdsl.SetDSLSemanticsTest;
import de.tud.stg.tigerseye.examples.setdsl.SetDSLTest;
import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSLTest;
import de.tud.stg.tigerseye.examples.statemachine.StateMachineDSLSemanticsTest;
import de.tud.stg.tigerseye.examples.statemachine.StateMachineDSLTest;
import de.tud.stg.tigerseye.examples.statemachine.StateMachineTest;
import de.tud.stg.tigerseye.examples.tinysql.TinySQLTest;
import de.tud.stg.tigerseye.examples.unitsdsl.UnitsDSLSemanticTest;
import de.tud.stg.tigerseye.examples.unitsdsl.UnitsDSLTest;


@RunWith(Suite.class)
@SuiteClasses({
	LogoDSLTest.class, //
	MapDSLTest.class, //
	LogoDSLTest.class, //
	UnitsDSLTest.class, //
	UnitsDSLSemanticTest.class, //
	SimpleSqlDSLTest.class, //
	SetDSLSemanticsTest.class, //
	SetDSLTest.class, //
	StateMachineTest.class, //
	StateMachineDSLSemanticsTest.class,//
	StateMachineDSLTest.class,//
	InterpreterCombinerTest.class, //
	TinySQLTest.class, //
	
})
public class AllTestsExampleDSLs {

}
