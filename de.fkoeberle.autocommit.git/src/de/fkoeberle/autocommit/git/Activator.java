package de.fkoeberle.autocommit.git;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;

public class Activator extends AbstractUIPlugin {
	private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void logError(String message, Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, AutoCommitPluginActivator.PLUGIN_ID,
						message, e));
	}
}
