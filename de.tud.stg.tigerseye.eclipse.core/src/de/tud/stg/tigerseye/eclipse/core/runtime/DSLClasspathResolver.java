package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;

import javax.annotation.CheckForNull;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds the defined classpath entries for DSLs. <br>
 * DSLs can be deployed as jar file or folder. In the latter case the
 * {@code MANIFEST.MF} file is parsed and its entries added to the found
 * classpath entries. <br>
 * A DSL can also be still in development in some Eclipse instance in which case
 * the {@code .classpath} file contains the correct classpath information.
 * 
 * @author Leo Roos
 * 
 */
public class DSLClasspathResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeDSLDefinitionsCPContainer.class);

    private final FileLocatorWrapper fileHelper;

    public DSLClasspathResolver() {
	this(new FileLocatorWrapper());
    }

    public DSLClasspathResolver(FileLocatorWrapper fileLocator) {
	this.fileHelper = fileLocator;
    }

    /*
     * private static final String metaInfDir = "META-INF";
     * 
     * private static final String projectRootDir = ".";
     * 
     * private static final String defaultBinName = "bin";
     * 
     * 
     * 
     * private final List<IClasspathEntry> cpEntries = new
     * ArrayList<IClasspathEntry>();
     * 
     * public void jumpintoclass() { List<DSLDefinition> dslDefinitions =
     * TigerseyeCore .getLanguageProvider().getDSLDefinitions(); Set<String>
     * iteratedContributor = new HashSet<String>(); for (DSLDefinition
     * dslDefinition : dslDefinitions) {
     * 
     * don't add same DSL from different contributors twice
     * 
     * if (dslDefinition.isActive() &&
     * !iteratedContributor.contains(dslDefinition
     * .getContributorSymbolicName())) { try {
     * addProjectPathForDSL(dslDefinition); } catch (IOException e) {
     * logger.error("Failed to add dsl  {} to classpath", dslDefinition, e); }
     * iteratedContributor.add(dslDefinition .getContributorSymbolicName()); } }
     * 
     * logger.trace("added classpathentries: {}", this.cpEntries); }
     * 
     * private void addProjectPathForDSL(DSLDefinition dslDefinition) throws
     * IOException { String contributorSymbolicName = dslDefinition
     * .getContributorSymbolicName(); Bundle bundle =
     * Platform.getBundle(contributorSymbolicName); File bundleFile =
     * FileLocator.getBundleFile(bundle); File buildProps = new File(bundleFile,
     * "build.properties"); Properties properties = new Properties();
     * FileInputStream fileInputStream = null; try { fileInputStream = new
     * FileInputStream(buildProps); properties.load(fileInputStream); } catch
     * (Exception e) { IOUtils.closeQuietly(fileInputStream); } List<String>
     * dirFiles = new ArrayList<String>();
     * 
     * usually says nothing about classpath
     * 
     * // getResourceNamesForProperty(properties, "output.."); List<String>
     * includes = getResourceNamesForProperty(properties, "bin.includes");
     * 
     * List<File> jars = new LinkedList<File>(); for (String string : includes)
     * { File cpFile = new File(bundleFile, string); if (isJarFile(cpFile)) {
     * jars.add(cpFile); } else if (cpFile.isDirectory()) {
     * dirFiles.add(string); } } for (File jar : jars) {
     * addFileAsCPEntryIfExistant(jar); } List<File> classFolders =
     * getClassFolders(dirFiles, bundleFile); for (File cpFolderFile :
     * classFolders) { addFileAsCPEntryIfExistant(cpFolderFile); }
     * 
     * }
     * 
     * private List<String> getResourceNamesForProperty(Properties properties,
     * String property) { LinkedList<String> resources = new
     * LinkedList<String>(); String resourcesCSV =
     * properties.getProperty(property, null); if (resourcesCSV != null) {
     * String[] resourcesArray = resourcesCSV.split(","); for (String
     * resourceName : resourcesArray) { resources.add(resourceName); } } return
     * resources; }
     * 
     * private void addFileAsCPEntryIfExistant(File cpFile) { if
     * (cpFile.exists()) { Path cpEntryPath = new
     * Path(cpFile.getAbsolutePath()); IClasspathEntry newLibraryEntry =
     * JavaCore.newLibraryEntry( cpEntryPath, null, null);
     * this.cpEntries.add(newLibraryEntry); } }
     * 
     * private boolean isJarFile(File cpFile) { return cpFile.isFile() &&
     * cpFile.getName().endsWith(".jar"); }
     * 
     * private List<File> getClassFolders(List<String> cpDirFiles, File
     * bundleFile) {
     * 
     * TODO refactor: should consider includes+projectroot as default cp and the
     * Manifest classpath. Additionally to provide access during development
     * allow bin folder.
     * 
     * 
     * If class output folder exists it can not be nested inside a project root
     * folder which as well is declared as class folder. Such a combination will
     * cause unresolved class path problems. I assume that the relevant class
     * folder is the specifically as "output.." property defined folder.
     * 
     * int metaInfIndex = hasStringBeginningWith(cpDirFiles, metaInfDir); if
     * (metaInfIndex >= 0) { cpDirFiles.remove(metaInfIndex); } // int
     * stdBinIndex = hasStringBeginningWith(cpDirFiles, defaultBinName); //
     * boolean hasStdBin = stdBinIndex >= 0; // if (!hasStdBin) { boolean
     * hasStdBin = containsBinFolder(bundleFile); if (hasStdBin) {
     * cpDirFiles.add(defaultBinName); // } } else {
     * cpDirFiles.add(projectRootDir); } int rootCPIndex =
     * hasStringBeginningWith(cpDirFiles, projectRootDir); boolean hasRootCP =
     * rootCPIndex >= 0; if (hasRootCP && hasStdBin) {
     * cpDirFiles.remove(rootCPIndex); hasRootCP = false; }
     * 
     * List<File> classFolder = new ArrayList<File>(); for (String string :
     * cpDirFiles) { File cpFolderFile = new File(bundleFile, string); // TODO
     * refactor this is just a quick and dirty workaround if
     * (!string.startsWith(projectRootDir) && hasRootCP &&
     * cpFolderFile.isDirectory()) { logger.trace(
     * "not adding {} to avoid nesting conflicts since root directory is also on classpath"
     * , string); } else { classFolder.add(cpFolderFile); } } return
     * classFolder; }
     *//**
     * Returns error code depending on whether {@code beginningWith} is
     * contained in {@code cpDirFiles} List.
     * 
     * @param cpDirFiles
     * @param beginningWith
     * @return <ul>
     *         <li>{@code -1} if no string beginning with {@code beginningWith}
     *         is contained
     *         <li>a integer greater or equal {@code 0} if {@code beginningWith}
     *         is contained. The integer represents the entry that starts with
     *         the {@code beginningWith} string.
     *         </ul>
     */
    /*
     * private int hasStringBeginningWith(List<String> cpDirFiles, String
     * beginningWith) { for (int i = 0; i < cpDirFiles.size(); i++) { if
     * (cpDirFiles.get(i).startsWith(beginningWith)) return i; } return -1; }
     * 
     * private boolean containsBinFolder(File bundleFile) { String[] list =
     * bundleFile.list(); for (String string : list) { if
     * (string.equals(defaultBinName)) return true; } return false; }
     */

    /**
     * Resolves the <i>probable</i> classpath for {@code bundle}.
     * <i>Probable</i> because it makes assumption regarding when the bundle is
     * still in a development location.
     * 
     * @param bundle
     *            Bundle to resolve classpath for.
     * @return the classpath of {@code bundle} which <i>should</i> not be empty.
     *         Returns <code>null</code> if no valid classpath was found.
     */
    public @CheckForNull
    File[] resolveCPEntriesForBundle(Bundle bundle) {

	File bundleFile = null;
	try {
	    bundleFile = fileHelper.getBundleFile(bundle);
	} catch (IOException e) {
	    logger.debug("No classpath for bundle {} could be resolved", bundle);
	    return null;
	}

	if (bundleFile.isFile()) {
	    if (FileHelper.isJar(bundleFile.getName())) {
		return new File[] { bundleFile };
	    } else {
		logger.debug("unknown format of file: ", bundleFile);
		return null;
	    }
	}

	// TODO check for .classpathfile

	String[] manifestClassPathEntries = getManifestClassPathEntries(bundle);
	File[] result = new File[manifestClassPathEntries.length];
	for (int i = 0; i < manifestClassPathEntries.length; i++) {
	    result[i] = new File(bundleFile, manifestClassPathEntries[i]);
	}
	return result;
    }

    private String[] getManifestClassPathEntries(Bundle bundle) {
	@SuppressWarnings("unchecked")
	// stated by documentation to be <String, String>
	Dictionary<String, String> manifestEntries = bundle.getHeaders();
	String cpEntries = manifestEntries.get(Constants.BUNDLE_CLASSPATH);

	String[] result;
	if (cpEntries == null)
	    // if no classpath-entry is available, the default classpath is used
	    result = new String[] { "." };
	else
	    result = cpEntries.split(",");

	assert result.length > 0;
	return result;
    }

}
