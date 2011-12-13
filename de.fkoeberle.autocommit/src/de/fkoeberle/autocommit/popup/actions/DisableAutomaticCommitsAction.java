package de.fkoeberle.autocommit.popup.actions;

import static de.fkoeberle.autocommit.popup.actions.ProjectsWithNatureSearchUtil.searchProjectsWithEnabledState;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import de.fkoeberle.autocommit.Nature;

public class DisableAutomaticCommitsAction implements IObjectActionDelegate {

	private Shell shell;
	private Set<IProject> selectedProjectsWithNature;

	public DisableAutomaticCommitsAction() {
		super();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	@Override
	public void run(IAction action) {
		try {
			for (IProject project : selectedProjectsWithNature) {
				Nature.removeSelfFrom(project);
			}
		} catch (CoreException e) {
			String message = "Failed to enable automatic commits";
			MessageDialog.openError(shell, message,
					"An error occored, view log for details");
			AutoCommitPluginActivator.logError(message, e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedProjectsWithNature = searchProjectsWithEnabledState(selection,
				true);
		action.setEnabled(!selectedProjectsWithNature.isEmpty());
	}
}
