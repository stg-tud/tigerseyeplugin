package de.tud.stg.popart.dslsupport.sql.dsjpm;

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.DeleteStatement;

/**
 * Marks the point in the execution when a delete statement is to be executed
 */
public class DeleteJoinPoint extends SQLJoinPoint {
	DeleteStatement deleteStatement;

	public DeleteJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}

}
