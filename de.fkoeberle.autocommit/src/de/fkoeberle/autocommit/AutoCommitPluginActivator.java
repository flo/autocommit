/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * Can be used to iterate over the repositories with enabled autocommit support.
 */
public class AutoCommitPluginActivator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.fkoeberle.autocommit"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = "de.fkoeberle.autocommit.vcs";

	// The shared instance
	private static AutoCommitPluginActivator plugin;

	private volatile List<IVersionControlSystem> versionControlSystems;
	private IRegistryEventListener registryEventListener;
	private IResourceChangeListener resourceChangeListener;
	private List<IAutoCommitEnabledStateListener> autoCommitEnabledStateListenerList;

	/**
	 * The constructor
	 */
	public AutoCommitPluginActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registryEventListener = new RegistryEventListener();
		Platform.getExtensionRegistry().addListener(registryEventListener);
		updateVersionControlSystemsList();
		resourceChangeListener = new UpdateAutocommitNatureScheduler();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(resourceChangeListener,
				IResourceChangeEvent.POST_CHANGE);
		autoCommitEnabledStateListenerList = new ArrayList<IAutoCommitEnabledStateListener>();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			Platform.getExtensionRegistry().removeListener(
					registryEventListener);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.removeResourceChangeListener(resourceChangeListener);
		} finally {
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AutoCommitPluginActivator getDefault() {
		return plugin;
	}

	/**
	 * Commits changes in all repositories which belong to projects which are
	 * enabled for automatic commits.
	 */
	public void commit() {
		for (IRepository repository : getAllEnabledRepositories()) {
			try {
				repository.commit();
			} catch (IOException e) {
				logError(
						"An exception occured while automatically commiting to a repository",
						e);
			}
		}
	}

	public IRepository getRepositoryFor(IProject project) {
		for (IVersionControlSystem versionControlSystem : versionControlSystems) {
			IRepository repository = versionControlSystem
					.getRepositoryFor(project);
			if (repository != null) {
				return repository;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return all {@link IProject}s in the workspace which are enabled for
	 *         automatic commits.
	 */
	private LinkedHashSet<IProject> getAllEnabledProjects() {
		final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		LinkedHashSet<IProject> enabledProjects = new LinkedHashSet<IProject>();
		for (IProject project : allProjects) {
			try {
				if (project.exists() && project.isOpen()
						&& project.hasNature(Nature.ID)) {
					enabledProjects.add(project);
				}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		return enabledProjects;
	}

	public LinkedHashSet<IRepository> getAllEnabledRepositories() {
		LinkedHashSet<IProject> allEnabledProjects = getAllEnabledProjects();
		return getRepositoriesFor(allEnabledProjects);
	}

	private LinkedHashSet<IRepository> getRepositoriesFor(
			Iterable<IProject> projects) {
		LinkedHashSet<IRepository> repositories = new LinkedHashSet<IRepository>();
		for (IVersionControlSystem vcs : versionControlSystems) {
			for (IProject project : projects) {
				IRepository repository = vcs.getRepositoryFor(project);
				if (repository != null) {
					repositories.add(repository);
				}
				repositories.add(vcs.getRepositoryFor(project));
			}
		}
		return repositories;
	}

	public void updateVersionControlSystemsList() {
		List<IVersionControlSystem> newList = new ArrayList<IVersionControlSystem>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (IConfigurationElement e : elements) {
			try {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IVersionControlSystem) {
					IVersionControlSystem vcs = (IVersionControlSystem) o;
					newList.add(vcs);
				}
			} catch (CoreException ex) {
				logError(
						"An exception occured while updating the list of available version control systems for automatic commits.",
						ex);
			}
		}
		versionControlSystems = newList;
	}

	private final class UpdateAutocommitNatureScheduler implements
			IResourceChangeListener {
		UpdateAutocommitNatureJob job = new UpdateAutocommitNatureJob(
				AutoCommitPluginActivator.this);

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				ProjectSetGatherer gatherer = new ProjectSetGatherer();
				event.getDelta().accept(
						gatherer,
						IContainer.INCLUDE_HIDDEN
								| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS
								| IContainer.INCLUDE_PHANTOMS);
				job.getProjects().addAll(gatherer.getProjects());
				job.schedule();
			} catch (CoreException e) {
				logError(
						"Catched an CoreException while listening for resource changes to update autocommit natures",
						e);
				throw new RuntimeException(e);
			}
		}
	}

	private final class RegistryEventListener implements IRegistryEventListener {
		@Override
		public void removed(IExtensionPoint[] extensionPoints) {
			// do nothing
		}

		@Override
		public void removed(IExtension[] extensions) {
			updateVersionControlSystemsList();
		}

		@Override
		public void added(IExtensionPoint[] extensionPoints) {
			// do nothing
		}

		@Override
		public void added(IExtension[] extensions) {
			updateVersionControlSystemsList();
		}
	}

	private void fireAutoCommitEnabledStateChanged(IProject project) {
		for (IAutoCommitEnabledStateListener listener : autoCommitEnabledStateListenerList) {
			listener.handleEnabledStateChanged(project);
		}
	}

	public void addAutoCommitEnabledStateListener(
			IAutoCommitEnabledStateListener listener) {
		autoCommitEnabledStateListenerList.add(listener);
	}

	public void removeAutoCommitEnabledStateListener(
			IAutoCommitEnabledStateListener listener) {
		autoCommitEnabledStateListenerList.remove(listener);
	}

	public void disableAutoCommitsFor(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] natureIds = projectDescription.getNatureIds();
		for (int i = 0; i < natureIds.length; i++) {
			if (natureIds[i] == Nature.ID) {
				natureIds[natureIds.length - 1] = natureIds[i];
				break;
			}
		}
		natureIds = Arrays.copyOf(natureIds, natureIds.length - 1);
		projectDescription.setNatureIds(natureIds);
		project.setDescription(projectDescription, null);
		fireAutoCommitEnabledStateChanged(project);
	}

	public void enableAutoCommitsFor(IProject project) throws CoreException,
			IOException {
		IProjectDescription projectDescription = project.getDescription();
		String[] natureIds = projectDescription.getNatureIds();
		natureIds = Arrays.copyOf(natureIds, natureIds.length + 1);
		natureIds[natureIds.length - 1] = Nature.ID;
		projectDescription.setNatureIds(natureIds);
		project.setDescription(projectDescription, null);
		for (IVersionControlSystem vcs : versionControlSystems) {
			IRepository repository = vcs.getRepositoryFor(project);
			if (repository != null) {
				repository.prepareProjectForAutomaticCommits(project);
			}
		}
		fireAutoCommitEnabledStateChanged(project);
	}

	public static void logError(String message, Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, AutoCommitPluginActivator.PLUGIN_ID,
						message, e));
	}

	public static void logUnexpectedException(CoreException e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, AutoCommitPluginActivator.PLUGIN_ID,
						"Unexpected exception", e));
	}

}
