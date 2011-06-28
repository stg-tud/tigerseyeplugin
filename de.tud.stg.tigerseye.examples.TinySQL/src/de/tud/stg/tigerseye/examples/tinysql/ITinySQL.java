package de.tud.stg.tigerseye.examples.tinysql;

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

@de.tud.stg.popart.builder.core.annotations.DSL(whitespaceEscape=" ")
public interface ITinySQL extends DSL{

		@DSLMethod(prettyName="SELECT p0 FROM p1")
		@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
		List<Map> selectFrom(String[] columns, String[] tables);

}
