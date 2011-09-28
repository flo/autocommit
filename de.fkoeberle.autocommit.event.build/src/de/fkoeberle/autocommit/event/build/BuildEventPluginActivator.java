package de.fkoeberle.autocommit.event.build;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BuildEventPluginActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "autocommit.event.build"; //$NON-NLS-1$
	private static BuildEventPluginActivator plugin;
	private IResourceChangeListener postBuildListener;

	public BuildEventPluginActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		postBuildListener = new PostBuildListener();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(postBuildListener,
				IResourceChangeEvent.POST_BUILD);
	}

	public void stop(BundleContext context) throws Exception {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.removeResourceChangeListener(postBuildListener);
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
	public static BuildEventPluginActivator getDefault() {
		return plugin;
	}

}
