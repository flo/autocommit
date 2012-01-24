/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

class SelectionSearchUtil {

	public static LinkedHashSet<IProject> getSelectedProjects(
			ISelection selection) {
		LinkedHashSet<IProject> projects = new LinkedHashSet<IProject>();
		if (!(selection instanceof IStructuredSelection)) {
			return projects;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Iterator<?> iterator = structuredSelection.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			IProject project = null;
			if (o instanceof IProject) {
				project = (IProject) o;
			} else if (o instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) o;
				project = (IProject) adaptable.getAdapter(IProject.class);
			}
			if (project != null) {
				projects.add(project);
			}
		}
		return projects;
	}

	/**
	 * 
	 * @param selection
	 *            the projects to check.
	 * @param enabledState
	 *            if true only projects in the specified selection will be
	 *            return which contain the nature {@link Nature}.
	 * 
	 * @return never null.
	 */
	public static Set<IProject> searchAutoCommitableProjectsWithEnabledState(
			ISelection selection, boolean enabledState) {
		Set<IProject> projects = new HashSet<IProject>();
		for (IProject project : getSelectedProjects(selection)) {
			try {
				if (project.isOpen()
						&& project.hasNature(Nature.ID) == enabledState) {
					IRepository repository = AutoCommitPluginActivator
							.getDefault().getRepositoryFor(project);
					if (repository != null) {
						projects.add(project);
					}
				}
			} catch (CoreException e) {
				AutoCommitPluginActivator.logError(
						"Failed determine enabled state of context menu item",
						e);
				/*
				 * Allow the user to use the context menu so that he can get a
				 * proper error message or a good result anyway.
				 */
				projects.add(project);
			}
		}
		return projects;
	}
}
