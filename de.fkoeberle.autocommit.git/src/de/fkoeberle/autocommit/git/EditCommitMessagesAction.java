package de.fkoeberle.autocommit.git;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import de.fkoeberle.autocommit.IRepository;

public class EditCommitMessagesAction implements IObjectActionDelegate {

	private Shell shell;
	private IProject project;

	public EditCommitMessagesAction() {
		super();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	@Override
	public void run(IAction action) {
		try {
			IRepository repository = AutoCommitPluginActivator.getDefault()
					.getRepositoryFor(project);
			GitRepositoryAdapter repositoryAdapter = (GitRepositoryAdapter) repository;
			repositoryAdapter.editCommitMessagesFor(project);
		} catch (Exception e) {
			String message = "Failed to edit commit messages";
			MessageDialog.openError(shell, message,
					"An error occored, view log for details");
			AutoCommitPluginActivator.logError(message, e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		StructuredSelection structuredSelection = (StructuredSelection) selection;
		project = (IProject) (structuredSelection.iterator().next());
	}
}
