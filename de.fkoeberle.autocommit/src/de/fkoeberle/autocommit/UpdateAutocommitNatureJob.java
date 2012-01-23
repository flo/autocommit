/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

final class UpdateAutocommitNatureJob extends Job {

	private final AutoCommitPluginActivator pluginActivator;
	private final Set<IProject> projects;

	UpdateAutocommitNatureJob(
			AutoCommitPluginActivator autoCommitPluginActivator) {
		super("Update autocommit state");
		pluginActivator = autoCommitPluginActivator;
		this.projects = Collections.synchronizedSet(new HashSet<IProject>());
		setUser(false);
		setSystem(true);
		setPriority(Job.DECORATE);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			for (IProject project : projects) {
				IRepository repository = pluginActivator
						.getRepositoryFor(project);

				if (project.exists() && project.isOpen()
						&& (repository == null) && project.hasNature(Nature.ID)) {
					AutoCommitPluginActivator.getDefault()
							.disableAutoCommitsFor(project);
				}
			}
			return Status.OK_STATUS;
		} catch (CoreException e) {
			return new Status(IStatus.ERROR,
					AutoCommitPluginActivator.PLUGIN_ID,
					"Failed to update which projects can autocommit", e);
		}
	}

	@Override
	public boolean shouldRun() {
		return projects.size() > 0;
	}

	@Override
	public boolean shouldSchedule() {
		return projects.size() > 0;
	}

	/**
	 * 
	 * @return a synchronized sets of projects to update.
	 */
	public Set<IProject> getProjects() {
		return projects;
	}
}