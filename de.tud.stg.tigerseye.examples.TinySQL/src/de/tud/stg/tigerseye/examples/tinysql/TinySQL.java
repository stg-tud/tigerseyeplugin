package de.tud.stg.tigerseye.examples.tinysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.Interpreter;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

public class TinySQL extends Interpreter implements ITinySQL {

	public class SQLConnection {

		public List<Map> rows(String query) {

			System.out.println("performing Query:" + query);

			Map<String, String> map = new HashMap<String, String>();
			map.put("Name", "John Doe");
			map.put("Age", "0..122");

			ArrayList<Map> list = new ArrayList<Map>();
			list.add(map);
			return list;
		}

	}

	private SQLConnection sqlConnection = new SQLConnection();

	/*
	 * FIXME(Leo Roos;Jun 28, 2011): as described in #70 the annotations should
	 * not be necessary here, since they are already declared in the ITinySQL
	 * interface
	 */
	@DSLMethod(prettyName = "SELECT p0 FROM p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	@Override
	public List<Map> selectFrom(String[] columns, String[] tables) {
		// check arguments to be correct
		String query = "SELECT " + implode(",", columns);
		query += " FROM " + implode(",", tables);
		return sqlConnection.rows(query);
	}

	private String implode(String delimiter, String[] strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append(string).append(",");
		}
		return sb.toString();
	}

}
