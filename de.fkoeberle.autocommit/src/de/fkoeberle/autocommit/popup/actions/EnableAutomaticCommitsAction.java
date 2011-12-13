package de.fkoeberle.autocommit.popup.actions;

import static de.fkoeberle.autocommit.popup.actions.ProjectsWithNatureSearchUtil.searchAutoCommitableProjectsWithEnabledState;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;

public class EnableAutomaticCommitsAction implements IObjectActionDelegate {

	private Shell shell;
	private Set<IProject> selectedProjectsWithoutNature;

	/**
	 * Constructor for Action1.
	 */
	public EnableAutomaticCommitsAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action) {
		try {
			for (IProject project : selectedProjectsWithoutNature) {
				AutoCommitPluginActivator.getDefault().enableAutoCommitsFor(
						project);
			}
		} catch (Exception e) {
			String message = "Failed to enable automatic commits";
			MessageDialog.openError(shell, message,
					"An error occored, view log for details");
			AutoCommitPluginActivator.logError(message, e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedProjectsWithoutNature = searchAutoCommitableProjectsWithEnabledState(
				selection, false);
		action.setEnabled(!selectedProjectsWithoutNature.isEmpty());
	}
}
