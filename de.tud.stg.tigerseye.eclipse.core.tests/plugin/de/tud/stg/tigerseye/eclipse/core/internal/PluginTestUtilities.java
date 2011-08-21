package de.tud.stg.tigerseye.eclipse.core.internal;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.tud.stg.tigerseye.eclipse.core.runtime.ProjectLinker;

public class PluginTestUtilities {

	static void linkExampleProject() throws URISyntaxException,
			CoreException, InterruptedException {
		String projectName = "de.tud.stg.tigerseye.examples.WorkspaceLanguage";
		URI exampelWSProject = new URI("file:///home/leo/wss/runtime-EclipseApplication42/"
				+ projectName);		
		IProject linkProject = new ProjectLinker().linkProject(exampelWSProject, projectName);
		linkProject.open(new NullProgressMonitor());	
		while(!linkProject.isOpen()){
			Thread.sleep(5);
		}
	}

}
