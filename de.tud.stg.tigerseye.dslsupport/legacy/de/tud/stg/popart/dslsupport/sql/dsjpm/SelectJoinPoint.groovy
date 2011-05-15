package de.tud.stg.popart.dslsupport.sql.dsjpm

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.*;

/**
 * Marks the point in the execution when a query is to be executed
 */
class SelectJoinPoint extends SQLJoinPoint {
	Query query
	
	public SelectJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}
}
