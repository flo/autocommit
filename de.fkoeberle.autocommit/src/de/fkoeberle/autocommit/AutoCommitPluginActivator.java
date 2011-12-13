package de.fkoeberle.autocommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * Can be used to iterate over the repositories with enabled autocommit support.
 */
public class AutoCommitPluginActivator extends AbstractUIPlugin implements
		Iterable<IRepository> {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.fkoeberle.autocommit"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = "de.fkoeberle.autocommit.vcs";

	// The shared instance
	private static AutoCommitPluginActivator plugin;

	private List<IVersionControlSystem> versionControlSystems;
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

	public synchronized void updateVersionControlSystemsList() {
		versionControlSystems = new ArrayList<IVersionControlSystem>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (IConfigurationElement e : elements) {
			try {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IVersionControlSystem) {
					IVersionControlSystem vcs = (IVersionControlSystem) o;
					versionControlSystems.add(vcs);
				}
			} catch (CoreException ex) {
				logException(
						"An exception occured while updating the list of available version control systems for automatic commits.",
						ex);
			}
		}
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public synchronized void commitIfPossible() {
		UIJob job = new UIJob("Auto Commit") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {

				for (IWorkbenchWindow window : PlatformUI.getWorkbench()
						.getWorkbenchWindows()) {
					for (IWorkbenchPage page : window.getPages()) {
						IEditorPart[] dirtyEditors = page.getDirtyEditors();
						if (dirtyEditors.length > 0) {
							logInfo(String
									.format("Not committing since there unsaved changes"));
							return Status.OK_STATUS;
						}
					}
				}
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				int maxProblemServity;
				try {
					maxProblemServity = root.findMaxProblemSeverity(
							IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					logException(
							"An exception occured while determining if there are problems for an auto commit.",
							e);
					return new Status(
							IStatus.ERROR,
							AutoCommitPluginActivator.PLUGIN_ID,
							"An exception occured while determining if there are problems for an auto commit.",
							e);
				}
				if (maxProblemServity == IMarker.SEVERITY_ERROR) {
					logInfo(String
							.format("Not committing since there are problem markers"));
					return Status.OK_STATUS;
				}

				for (IVersionControlSystem vcs : versionControlSystems) {
					for (IRepository repository : vcs) {
						repository.commit();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.schedule();

	}

	private void logException(String message, Exception e) {
		getLog().log(
				new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
	}

	private void logInfo(String message) {
		getLog().log(
				new Status(Status.INFO, PLUGIN_ID, Status.OK, message, null));
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
				logException(
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

	@Override
	public Iterator<IRepository> iterator() {
		return new Iterator<IRepository>() {
			private final Iterator<IVersionControlSystem> vcsIterator = versionControlSystems
					.iterator();
			private Iterator<IRepository> repositoryIterator = null;

			private void ensureRepositoryIteratorHasNextOrIsNull() {
				while (repositoryIterator == null
						|| (!repositoryIterator.hasNext() && vcsIterator
								.hasNext())) {
					repositoryIterator = vcsIterator.next().iterator();
				}
				if (repositoryIterator != null && !repositoryIterator.hasNext()) {
					repositoryIterator = null;
				}
			}

			@Override
			public boolean hasNext() {
				ensureRepositoryIteratorHasNextOrIsNull();

				return repositoryIterator != null;
			}

			@Override
			public IRepository next() {
				ensureRepositoryIteratorHasNextOrIsNull();
				if (repositoryIterator == null) {
					throw new NoSuchElementException();
				}
				return repositoryIterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
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
			vcs.prepareProjectForAutocommits(project);
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
