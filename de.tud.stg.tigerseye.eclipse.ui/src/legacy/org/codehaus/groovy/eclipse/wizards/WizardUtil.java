/*
 * Created on 27-Jan-2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package legacy.org.codehaus.groovy.eclipse.wizards;

import java.io.InputStream;
import java.util.ArrayList;

import legacy.org.codehaus.groovy.eclipse.ui.ArtifactCodeGenerator;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author MelamedZ
 */
public class WizardUtil {

    public final static String[] primitiveTypes = { "byte", "short", "int",
	    "long", "float", "double", "boolean", "char" };

    private static ArrayList<String> createdTypes = new ArrayList<String>();

    public static IFile createGroovyType(IPackageFragmentRoot root,
	    IPackageFragment pack, String cuName, String source)
	    throws CoreException {

	return createGroovyType(root, pack, cuName, source, true);
    }

    public static IFile createGroovyType(IPackageFragmentRoot root,
	    IPackageFragment pack, String cuName, String source,
	    boolean createPackageStatement) throws CoreException {

	checkPackageExists(root, pack);

	StringBuffer buf = new StringBuffer();
	if (createPackageStatement) {
	    if (pack.getElementName().length() > 0) {
		buf.append("package " + pack.getElementName() + "\n");
	    }
	    buf.append("\n");
	}
	buf.append(source);

	IContainer folder = (IContainer) pack.getResource();
	InputStream stream = null;
	try {
	    stream = IOUtils.toInputStream(buf.toString());
	    return createFile(folder, cuName, stream);
	} finally {
	    IOUtils.closeQuietly(stream);
	}
    }

    public static IFile createGroovyType(final IPackageFragment pack,
	    final String cuName, final String source) throws CoreException {
	final IContainer folder = (IContainer) pack.getResource();
	InputStream stream = null;
	try {
	    stream = IOUtils.toInputStream(source);
	    return createFile(folder, cuName, stream);
	} finally {
	    IOUtils.closeQuietly(stream);
	}
    }

    // import org.codehaus.groovy.eclipse.ui.util.JavaModelUtility;
    // public static String getSuperName(IPackageFragment pack, String
    // superClass)
    // throws JavaModelException {
    // if (superClass != null && superClass.length() > 0
    // && !superClass.equals("java.lang.Object")) {
    // IType type = JavaModelUtility.findType(pack.getJavaProject(),
    // superClass);
    // if (type != null) {
    // return type.getElementName();
    // }
    // }
    //
    // return "";
    // }

    private static IFile createFile(IContainer folder, String name,
	    InputStream contents) throws JavaModelException {
	IFile file = folder.getFile(new Path(name));
	try {
	    file.create(contents, IResource.FORCE, null);
	} catch (CoreException e) {
	    throw new JavaModelException(e);
	}

	return file;
    }

    /**
     * Creates the super type of a keyword if the type does not already exist
     * Primitive Types are ignored. If the type can be found in java.lang /
     * java.util it is also ignored
     */
    public static void createType(IJavaProject project,
	    IPackageFragment packagefragment, IPackageFragmentRoot root,
	    String type, boolean isAbstract) {
	if (!createdTypes.contains(type) && !isPrimitiveType(type)
		&& !searchClass(type)) {

	    createdTypes.add(type);

	    String signature = (isAbstract) ? "public abstract class " + type
		    + " { " : "public class " + type + " { ";

	    ArtifactCodeGenerator gen = new ArtifactCodeGenerator(project);
	    gen.addCode(signature);
	    gen.addLineBreak();
	    gen.addLineBreak();
	    gen.addCode("}");

	    try {
		WizardUtil.createGroovyType(root, packagefragment, type
			+ ".groovy", gen.toString());
	    } catch (CoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    /**
     * Return true if type is a primitive type
     * 
     * @param type
     * @return
     */
    private static boolean isPrimitiveType(String type) {
	for (int i = 0; i < primitiveTypes.length; i++) {
	    if (primitiveTypes[i].equals(type)) {
		return true;
	    }
	}
	return false;
    }

	/**
     * Searches class for a given String.
     * 
     * @param type
     * @return
     */
    public static boolean searchClass(String type) {
	return findClass(type) || findClass("java.lang." + type)
		|| findClass("java.util." + type);
    }

	/**
     * Return true if Class can be found.
     * 
     * @param type
     * @return
     */
    private static boolean findClass(String type) {
	try {
	    Class.forName(type);
	    return true;
	} catch (ClassNotFoundException e) {
	    return false;
	}
    }

	/**
     * Checks that the specified package fragment exists, and if it doesn't
     * creates it.
     * 
     * @param root
     *            source folder
     * @param pack
     *            package
     * @throws JavaModelException
     *             if an error occurs
     */
    private static void checkPackageExists(IPackageFragmentRoot root,
	    IPackageFragment pack) throws JavaModelException {

		if (pack == null) {
	    pack = root.getPackageFragment(""); // default package
	}

		if (!pack.exists()) {
	    final String packName = pack.getElementName();
	    pack = root.createPackageFragment(packName, true, null);
	}
    }

    /**
     * Clears the types created by this utility class. These types include
     * literal classes, supertypes of these classes and the return types
     */
    public static void clearCreatedTypes() {
	createdTypes.clear();
    }

}
