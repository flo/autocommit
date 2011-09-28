package de.fkoeberle.autocommit.event.build;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

public class PostBuildListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		AutoCommitPluginActivator.getDefault().commitIfPossible(null);
	}

}
