package de.fkoeberle.autocommit;

import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

final class AutoCommitJob extends Job {
	AutoCommitJob() {
		super("Auto Commit");
	}

	private boolean noUnsavedContentExists() {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				IEditorPart[] dirtyEditors = page.getDirtyEditors();
				if (dirtyEditors.length > 0) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean noBuildErrorsExist() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		int maxProblemServity;
		try {
			maxProblemServity = root.findMaxProblemSeverity(
					IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		return (maxProblemServity != IMarker.SEVERITY_ERROR);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (noUnsavedContentExists() && noBuildErrorsExist()) {
			commit();
		}
		return Status.OK_STATUS;
	}

	private void commit() {
		AutoCommitPluginActivator activator = AutoCommitPluginActivator
				.getDefault();
		for (IRepository repository : activator.getAllEnabledRepositories()) {
			try {
				repository.commit();
			} catch (IOException e) {
				activator
						.logException(
								"An exception occured while automatically commiting to a repository",
								e);
			}
		}
	}
}