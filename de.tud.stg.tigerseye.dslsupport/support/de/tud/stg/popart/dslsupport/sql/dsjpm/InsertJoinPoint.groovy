package de.tud.stg.popart.dslsupport.sql.dsjpm;

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.InsertStatement;

/**
 * Marks the point in the execution when an insert statement is to be executed
 */
public class InsertJoinPoint extends SQLJoinPoint {
	
	InsertStatement insertStatement;

	public InsertJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}

}
