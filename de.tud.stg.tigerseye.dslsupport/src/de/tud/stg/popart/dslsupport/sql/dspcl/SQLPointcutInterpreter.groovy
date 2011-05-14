package de.tud.stg.popart.dslsupport.sql.dspcl

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.pointcuts.Pointcut;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationActivator;

import de.tud.stg.popart.dslsupport.sql.SimpleSQL;
import de.tud.stg.popart.dslsupport.sql.dsjpm.DeleteJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.sql.dsjpm.InsertJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.sql.dsjpm.SelectJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.sql.dsjpm.UpdateJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import static de.tud.stg.popart.dslsupport.sql.ListHelper.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.ANY;
import static de.tud.stg.popart.dslsupport.sql.model.AnyTableReference.ANY_TABLE;

/**
 * This is the interpreter for the pointcut language for SQL.
 */
class SQLPointcutInterpreter extends PointcutDSL {
	private static SQLPointcutInterpreter instance
	
	public static SQLPointcutInterpreter getInstance() {
		if (instance == null) {
			instance = new SQLPointcutInterpreter()
		}
		return instance
	}
	
	/**
	 * Register the join point types for SQL.
	 */
	public SQLPointcutInterpreter() {
		InstrumentationActivator.declareJoinPoint(SimpleSQL.class, "query", SelectJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(SimpleSQL.class, "update", UpdateJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(SimpleSQL.class, "insert", InsertJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(SimpleSQL.class, "delete", DeleteJoinPointInstrumentation.class);
	}
	
	/**
	 * <p>Matches all select joinpoints which have an equal list of columns. Order is irrelevant to equality.</p>
	 * <p>If you want to match against a subset only, use {@link AnyColumnReference#ANY} as one of the members.</p>
	 * <p><b>Example:</b><br />
	 * Original list of columns: [foo, bar, zorg]<br />
	 * <table>
	 * <tr><th>argument</th><th>successful match</th></tr>
	 * <tr><td>[bar]</td><td>no</td></tr>
	 * <tr><td>[bar, ANY]</td><td>yes</td></tr>
	 * <tr><td>[bar, foo, zorg]</td><td>yes</td></tr>
	 * <tr><td>no argument</td><td>yes</td></tr>
	 * </table></p>
	 * <p>Note how omitting the argument is treated as [ANY]</p>
	 * @return the pointcut
	 */
	public Pointcut pselect(List<ColumnReference> selectList = [ANY]) {
		new SelectPCD(convertToColumnReferenceList(selectList));
	}
	
	/**
	 * Delegates to {@link #pselect(List<ColumnReference>)}
	 */
	public Pointcut pSELECT(List<ColumnReference> selectList = [ANY]) {
		return pselect(selectList);
	}

	/**
	 * <p>Matches all select joinpoints which have an equal list of tables. Order is irrelevant to equality.</p>
	 * <p>If you want to match against a subset only, use {@link AnyTableReference#ANY_TABLE} as one of the members.</p>
	 * <p><b>Example:</b><br />
	 * Original list of tables: [foo, bar, zorg]<br />
	 * <table>
	 * <tr><th>argument</th><th>successful match</th></tr>
	 * <tr><td>[bar]</td><td>no</td></tr>
	 * <tr><td>[bar, ANY_TABLE]</td><td>yes</td></tr>
	 * <tr><td>[bar, foo, zorg]</td><td>yes</td></tr>
	 * <tr><td>no argument</td><td>yes</td></tr>
	 * </table></p>
	 * <p>Note how omitting the argument is treated as [ANY_TABLE]</p>
	 * @return the pointcut
	 */
	public Pointcut pfrom(List<TableReference> tableReferences = [ANY_TABLE]) {
		new FromPCD(convertToTableReferenceList(tableReferences));
	}
	
	/**
	 * Delegates to {@link #pfrom(List<TableReference>)}
	 */
	public Pointcut pFROM(List<TableReference> tableReferences = [ANY_TABLE]) {
		return pfrom(tableReferences);
	}
		
	/**
	 * <p>Matches all update joinpoints which have an equal table</p>
	 * <p><b>Example:</b><br />
	 * Original table: foo<br />
	 * <table>
	 * <tr><th>argument</th><th>successful match</th></tr>
	 * <tr><td>foo</td><td>yes</td></tr>
	 * <tr><td>bar</td><td>no</td></tr>
	 * <tr><td>no argument</td><td>yes</td></tr>
	 * </table></p>
	 * <p>Note how omitting the argument or null is treated as a wildcard</p>
	 * @return the pointcut
	 */
	public Pointcut pupdate(QualifiedName tableName = null) {
		new UpdatePCD(tableName);
	}
	
	/**
	 * Delegates to {@link #pupdate(QualifiedName)}
	 */
	public Pointcut pUPDATE(QualifiedName tableName = null) {
		return pupdate(tableName);
	}

	/**
	 * Delegates to {@link #pupdate(QualifiedName)}
	 * @see #pupdate(QualifiedName)
	 */
	public Pointcut pupdate(Identifier tableName) {
		return pupdate(tableName as QualifiedName);
	}
	
	/**
	 * Delegates to {@link #pupdate(Identifier)}
	 */
	public Pointcut pUPDATE(Identifier tableName) {
		return pupdate(tableName as QualifiedName);
	}
	
	/**
	* <p>Matches all insert joinpoints which have an equal table</p>
	* <p><b>Example:</b><br />
	* Original table: foo<br />
	* <table>
	* <tr><th>argument</th><th>successful match</th></tr>
	* <tr><td>foo</td><td>yes</td></tr>
	* <tr><td>bar</td><td>no</td></tr>
	* <tr><td>no argument</td><td>yes</td></tr>
	* </table></p>
	* <p>Note how omitting the argument or null is treated as a wildcard</p>
	* @return the pointcut
	*/
	public Pointcut pinsert(QualifiedName tableName = null) {
		new InsertPCD(tableName);
	}
	
	/**
	 * Delegates to {@link #pinsert(QualifiedName)}
	 */
	public Pointcut pINSERT(QualifiedName tableName = null) {
		return pinsert(tableName);
	}

	/**
	 * Delegates to {@link #pinsert(QualifiedName)}
	 */
	public Pointcut pinsert(Identifier tableName) {
		return pinsert(tableName as QualifiedName);
	}
	
	/**
	 * Delegates to {@link #pinsert(Identifier)}
	 */
	public Pointcut pINSERT(Identifier tableName) {
		return pinsert(tableName as QualifiedName);
	}
	
	/**
	* <p>Matches all delete joinpoints which have an equal table</p>
	* <p><b>Example:</b><br />
	* Original table: foo<br />
	* <table>
	* <tr><th>argument</th><th>successful match</th></tr>
	* <tr><td>foo</td><td>yes</td></tr>
	* <tr><td>bar</td><td>no</td></tr>
	* <tr><td>no argument</td><td>yes</td></tr>
	* </table></p>
	* <p>Note how omitting the argument or null is treated as a wildcard</p>
	* @return the pointcut
	*/
	public Pointcut pdelete(QualifiedName tableName = null) {
		new DeletePCD(tableName);
	}

	/**
	 * Delegates to {@link #pdelete(QualifiedName)}
	 */
	public Pointcut pDELETE(QualifiedName tableName = null) {
		return pdelete(tableName)
	}
	
	/**
	 * Delegates to {@link #pdelete(QualifiedName)}
	 */
	public Pointcut pdelete(Identifier tableName) {
		return pdelete(tableName as QualifiedName);
	}

	/**
	 * Delegates to {@link #pdelete(QualifiedName)}
	 */
	public Pointcut pDELETE(Identifier tableName) {
		return pdelete(tableName as QualifiedName);
	}
}
