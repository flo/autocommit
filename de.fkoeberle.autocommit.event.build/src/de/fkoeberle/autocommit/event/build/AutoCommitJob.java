/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.event.build;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;

final class AutoCommitJob extends Job {
	AutoCommitJob() {
		super("Auto Commit");
	}

	private static class CheckForUnsavedContentRunnable implements Runnable {
		public volatile boolean noUnsavedContentExists;

		@Override
		public void run() {
			noUnsavedContentExists = checkIfNoUnsavedContentExists();
		}

		private boolean checkIfNoUnsavedContentExists() {
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
	}

	private boolean noUnsavedContentExists() {
		CheckForUnsavedContentRunnable runnable = new CheckForUnsavedContentRunnable();
		Display.getDefault().syncExec(runnable);
		return runnable.noUnsavedContentExists;
	}

	private boolean noBuildErrorsExist() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		int maxProblemServity;
		try {
			maxProblemServity = root.findMaxProblemSeverity(IMarker.PROBLEM,
					true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		return (maxProblemServity != IMarker.SEVERITY_ERROR);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (noUnsavedContentExists() && noBuildErrorsExist()) {
			AutoCommitPluginActivator.getDefault().commit();
		}
		return Status.OK_STATUS;
	}

}