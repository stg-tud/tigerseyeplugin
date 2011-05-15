package de.tud.stg.popart.dslsupport.sql.dsjpm

import java.util.Map;

import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * Abstract parent of all SQL joinpoints
 */
abstract class SQLJoinPoint extends JoinPoint {

	public SQLJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}
}
