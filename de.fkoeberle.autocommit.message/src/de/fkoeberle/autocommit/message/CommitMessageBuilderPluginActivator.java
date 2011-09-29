package de.fkoeberle.autocommit.message;

import java.util.Collection;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.eclipse.core.resources.IProject;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommitMessageBuilderPluginActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "de.fkoeberle.autocommit.message"; //$NON-NLS-1$
	private static CommitMessageBuilderPluginActivator plugin;
	
	public CommitMessageBuilderPluginActivator() {
	}

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
	public static CommitMessageBuilderPluginActivator getDefault() {
		return plugin;
	}
	
	public ICommitMessageBuilder createCommitMessageBuilder(Collection<IProject> project) {
		// TODO implement method createCommitMessageBuilder
		return null;
	}

}
