package utilities;

public class GlobalResourceCollection {

	/**
	 * Only the dslusupport.DSL specific classes
	 */
	public static final TestResource LogoDSLClasspath = new TestResource("LogoDSL/bin");
	/**
	 * The Logo library
	 */
	public static final TestResource JavalogoLibraryJar = new TestResource("LogoDSL/javalogo.jar");
	/**
	 * A complete <i>Eclipse-Workspace Java Project</i> containing a {@code .project} and {@code .classpath} file, as well 
	 * as resources referenced in those files. 
	 */
	public static final TestResource DSLDefinitionsDevelopmentProjectRoot = new TestResource("DSLDefinitionsDevelopmentProject");

}
