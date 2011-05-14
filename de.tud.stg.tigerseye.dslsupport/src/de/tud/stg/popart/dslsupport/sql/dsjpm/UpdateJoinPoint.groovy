package de.tud.stg.popart.dslsupport.sql.dsjpm;

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.UpdateStatement;

/**
 * Marks the point in the execution when an update statement is to be executed
 */
public class UpdateJoinPoint extends SQLJoinPoint {

	UpdateStatement updateStatement;
	
	public UpdateJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}

}
