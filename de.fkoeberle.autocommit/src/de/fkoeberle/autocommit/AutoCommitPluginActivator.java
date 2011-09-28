package de.fkoeberle.autocommit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
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
 */
public class AutoCommitPluginActivator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.fkoeberle.autocommit"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = "de.fkoeberle.autocommit.vcs";

	// The shared instance
	private static AutoCommitPluginActivator plugin;

	private List<IVersionControlSystem> versionControlSystems;
	private IRegistryEventListener registryEventListener;

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
				logException("An exception occured while updating the list of available version control systems for automatic commits.", ex);
			}
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			Platform.getExtensionRegistry().removeListener(
					registryEventListener);
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

	public synchronized void commitIfPossible(final String message) {
		UIJob job = new UIJob("Auto Commit") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// TODO ensure that it runs on UI thread: 
				// example when it's necessary: extracting a variable
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorPart[] dirtyEditors = page.getDirtyEditors();
				if (dirtyEditors.length > 0) {
					logInfo(String.format(
							"Not committing as '%s' since there unsaved changes",
							message));
					return Status.OK_STATUS;
				}

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				int maxProblemServity;
				try {
					maxProblemServity = root.findMaxProblemSeverity(IMarker.PROBLEM,
							true, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					logException(
							"An exception occured while determining if there are problems for an auto commit.",
							e);
					return new Status(IStatus.ERROR,AutoCommitPluginActivator.PLUGIN_ID,"An exception occured while determining if there are problems for an auto commit.",
							e);
				}
				if (maxProblemServity == IMarker.SEVERITY_ERROR) {
					logInfo(String.format(
							"Not committing as '%s' since there are problem markers",
							message));
					return Status.OK_STATUS;
				}

				for (IVersionControlSystem vcs : versionControlSystems) {
					vcs.commit(message);
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

	/**
	 * 
	 * @return true, if it could be verified that there are no uncomitted changes. If it fails to determine if there are changes it returns true.
	 */
	public synchronized boolean noUncommittedChangesExists() {
		for (IVersionControlSystem vcs : versionControlSystems) {
			if (!vcs.noUncommittedChangesExist()) {
				return false;
			}
		}
		return true;
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

}
