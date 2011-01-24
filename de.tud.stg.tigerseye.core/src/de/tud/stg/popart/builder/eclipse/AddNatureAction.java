package de.tud.stg.popart.builder.eclipse;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddNatureAction implements IObjectActionDelegate {
    private static final Logger logger = LoggerFactory.getLogger(AddNatureAction.class);


	private ISelection selection;

	public AddNatureAction() {
		logger.info("new AddNatureAction");
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
	    	logger.info("AddNatureAction run");
		final IStructuredSelection s = (IStructuredSelection) selection;
		final Object selected = s.getFirstElement();
		if (!(selected instanceof IProject) && !(selected instanceof IJavaProject)) {
			return;
		}

		final IProject targetProject = selected instanceof IProject ? (IProject) selected : ((IJavaProject) selected)
				.getProject();

		try {
			addNature(targetProject);
		} catch (CoreException e) {
			logger.warn("Generated log statement",e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private static void addNature(IProject project) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] ids = description.getNatureIds();
		final String[] newIds = (String[]) ArrayUtils.add(ids, DSLNature.TIGERSEYE_NATURE_ID);
		description.setNatureIds(newIds);
		project.setDescription(description, new NullProgressMonitor());
	}
}
