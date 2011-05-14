package de.tud.stg.popart.dslsupport.sql;

import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;

public class ListHelper {

	/**
	 * This method converts instances of {@link Identifier}, which are in
	 * the given list, to {@link ColumnReference}s. Any other types than
	 * Identifier and ColumnReference result in an AssertionError
	 * @param columns a list, that contains any types.
	 * @return a list, that only contains instances of {@link ColumnReference}
	 */
	public static List<ColumnReference> convertToColumnReferenceList(List columns){
		return columns.collect {
			switch(it.class){
				case ColumnReference: return it;
				case Identifier: return it as ColumnReference;
				default: throw new AssertionError();
			}
		};
	}
	
	/**
	* This method converts instances of {@link Identifier}, which are in
	* the given list, to {@link TableReference}s. Any other types than
	* Identifier and TableReference result in an AssertionError
	* @param columns a list, that contains any types.
	* @return a list, that only contains instances of {@link TableReference}
	*/
	public static List<TableReference> convertToTableReferenceList(List tables){
		return tables.collect {
			switch(it.class){
				case TableReference: return it;
				case Identifier: return it as TableReference;
				default: throw new AssertionError();
			}
		};
	}
}
