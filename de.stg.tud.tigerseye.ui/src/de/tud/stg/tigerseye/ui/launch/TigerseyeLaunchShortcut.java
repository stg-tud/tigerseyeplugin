package de.tud.stg.tigerseye.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut;
import org.eclipse.jface.operation.IRunnableContext;

public class TigerseyeLaunchShortcut extends JavaLaunchShortcut implements
	ILaunchShortcut {

    @Override
    protected ILaunchConfiguration createConfiguration(IType arg0) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected IType[] findTypes(Object[] arg0, IRunnableContext arg1)
	    throws InterruptedException, CoreException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected ILaunchConfigurationType getConfigurationType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected String getEditorEmptyMessage() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected String getSelectionEmptyMessage() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected String getTypeSelectionTitle() {
	// TODO Auto-generated method stub
	return null;
    }

}
