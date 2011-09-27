package de.fkoeberle.autocommit.event.refactor;

import org.eclipse.ltk.core.refactoring.IUndoManagerListener;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RefactorEventPluginActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "autocommit.event.refactor"; //$NON-NLS-1$
	private static RefactorEventPluginActivator plugin;
	private IUndoManagerListener refactoringUndoManagerListener;

	public RefactorEventPluginActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		refactoringUndoManagerListener = new RefactoringListener();
		RefactoringCore.getUndoManager().addListener(
				refactoringUndoManagerListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		try {
			RefactoringCore.getUndoManager().removeListener(
					refactoringUndoManagerListener);
		} finally {
			super.stop(context);
		}

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RefactorEventPluginActivator getDefault() {
		return plugin;
	}

}
